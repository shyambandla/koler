package com.chooloo.www.chooloolib.ui.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.WalletActivity
import com.chooloo.www.chooloolib.ui.aboutus.Aboutus
import com.google.android.material.navigation.NavigationView
import com.chooloo.www.chooloolib.ui.ads.SelectPreferencesActivity
import com.chooloo.www.chooloolib.ui.faq.FaqActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    private val sharedPrefFile = "kotlinsharedpreference"
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_account, container, false)
        val nav = v.findViewById<NavigationView>(R.id.navigationView) as NavigationView
        val header = nav.getHeaderView(0)
        if (activity != null) {
            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
                sharedPrefFile,
                Context.MODE_PRIVATE
            )
            val number = header.findViewById<TextView>(R.id.profile_number)
            val user = sharedPreferences.getString("user_number", "")
            number.text = user
        }
        val vv = LayoutInflater.from(context).inflate(R.layout.drawer_chip, null);

        vv?.findViewById<SwitchCompat>(R.id.toggleSwitch)
            ?.setOnCheckedChangeListener { _, isChecked ->
                Toast.makeText(
                    this.context,
                    "checkexc",
                    Toast.LENGTH_LONG
                ).show()
            }
        nav.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.my_wallet -> {
                    val intent = Intent(activity, WalletActivity::class.java)
                    startActivity(intent)
                }
                R.id.ad_preferences -> {
                    val intent = Intent(activity, SelectPreferencesActivity::class.java)
                    startActivity(intent)
                }
                R.id.log_out -> {

                    Toast.makeText(this.context, "Logout not implemented yet", Toast.LENGTH_LONG)
                        .show();
                }

                R.id.withdraw_amount -> {
                    val intent = Intent(activity, WalletActivity::class.java)
                    startActivity(intent)
                }
                R.id.about_us -> {
                    val intent = Intent(activity, FaqActivity::class.java)
                    startActivity(intent)
                }
                R.id.faq -> {
                    val intent = Intent(activity, Aboutus::class.java)
                    startActivity(intent)
                }

            }
            true
        }
        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}