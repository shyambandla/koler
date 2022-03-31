package com.chooloo.www.koler.ui.main

import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chooloo.www.chooloolib.interactor.prompt.PromptsInteractor
import com.chooloo.www.chooloolib.interactor.screen.ScreensInteractor
import com.chooloo.www.chooloolib.ui.base.BaseActivity
import com.chooloo.www.chooloolib.ui.contacts.ContactsViewState
import com.chooloo.www.chooloolib.ui.recents.RecentsViewState
import com.chooloo.www.koler.R
import com.chooloo.www.koler.databinding.MainBinding
import com.chooloo.www.koler.di.factory.fragment.FragmentFactory
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.chooloo.www.chooloolib.di.factory.fragment.FragmentFactory as ChoolooFragmentFactory

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewState>() {
    override val contentView by lazy { binding.root }
    override val viewState: MainViewState by viewModels()

    private val recentsViewState: RecentsViewState by viewModels()
    private val contactsViewState: ContactsViewState by viewModels()
    private val binding by lazy { MainBinding.inflate(layoutInflater) }
    private val _fragments by lazy { listOf(_contactsFragment, _recentsFragment,_accountFragment) }
    private val _recentsFragment by lazy { choolooFragmentFactory.getRecentsFragment() }
    private val _contactsFragment by lazy { choolooFragmentFactory.getContactsFragment() }
    private val _accountFragment by lazy { choolooFragmentFactory.getAccountFragment() }
    @Inject lateinit var prompts: PromptsInteractor
    @Inject lateinit var screens: ScreensInteractor
    @Inject lateinit var fragmentFactory: FragmentFactory
    @Inject lateinit var choolooFragmentFactory: ChoolooFragmentFactory



    override fun onSetup() {
        screens.disableKeyboard()
        val firAuth=FirebaseAuth.getInstance().currentUser;
        if(firAuth==null){
            val intent=Intent(applicationContext,OnBoardActivity::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }
        binding.apply {
            mainTabs.viewPager = mainViewPager
            with(mainTabs) { setHeadersResList(arrayOf(R.string.contacts, R.string.recents,R.string.preferences)) }

            mainMenuButton.setOnClickListener {
                viewState.onMenuClick()
            }

            mainDialpadButton.setOnClickListener {
                viewState.onDialpadFabClick()
            }

            mainSearchBar.setOnTextChangedListener { st ->
                contactsViewState.onFilterChanged(st)
                recentsViewState.onFilterChanged(st)
            }

            mainSearchBar.editText?.setOnFocusChangeListener { _, hasFocus ->
                viewState.onSearchFocusChange(hasFocus)
            }

            mainViewPager.adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount() = _fragments.size
                override fun createFragment(position: Int) = _fragments[position]
            }

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewState.currentPageIndex.value = position
                }
            })
        }

        viewState.apply {
            searchText.observe(this@MainActivity) {
                binding.mainSearchBar.text = it
            }

            searchHint.observe(this@MainActivity) {
                binding.mainSearchBar.hint = it.toString()
            }

            currentPageIndex.observe(this@MainActivity) {
                binding.mainViewPager.currentItem = it
            }

            isSearching.observe(this@MainActivity) {
                if (it) binding.root.transitionToState(R.id.constraint_set_main_collapsed)
            }

            showMenuEvent.observe(this@MainActivity) { ev ->
                ev.ifNew?.let { prompts.showFragment(fragmentFactory.getSettingsFragment()) }
            }

            showRecentEvent.observe(this@MainActivity) { ev ->
                ev.ifNew?.let {
                    prompts.showFragment(choolooFragmentFactory.getRecentFragment(it))
                }
            }

            showContactEvent.observe(this@MainActivity) { ev ->
                ev.ifNew?.let {
                    prompts.showFragment(choolooFragmentFactory.getBriefContactFragment(it))
                }
            }

            showDialerEvent.observe(this@MainActivity) { ev ->
                ev.ifNew?.let {
                    prompts.showFragment(
                        choolooFragmentFactory.getDialerFragment(it)
                    )
                }
            }
        }

        if (intent.action in arrayOf(Intent.ACTION_DIAL, Intent.ACTION_VIEW)) {
            viewState.onViewIntent(intent)
        }

        contactsViewState.itemClickedEvent.observe(this) {
            it.ifNew?.let(viewState::onContactItemClick)
        }

        recentsViewState.itemClickedEvent.observe(this) {
            it.ifNew?.let(viewState::onRecentItemClick)
        }
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        screens.ignoreEditTextFocus(event)
        return super.dispatchTouchEvent(event)
    }
}