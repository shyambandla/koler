package com.chooloo.www.koler.ui.main

import android.app.role.RoleManager
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.MotionEvent
import android.widget.Toast
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
     val REQUEST_CODE_SET_DEFAULT_DIALER=200


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
    }



    private fun checkDefaultDialer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage
        if (isAlreadyDefaultDialer)
            return
        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SET_DEFAULT_DIALER -> checkSetDefaultDialerResult(resultCode)
        }
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK       -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else            -> "Unexpected result code $resultCode"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        screens.ignoreEditTextFocus(event)
        return super.dispatchTouchEvent(event)
    }
}