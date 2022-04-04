package com.chooloo.www.chooloolib.ui.ads

import android.os.Bundle
import android.telecom.PhoneAccount
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.model.SimAccount

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.ad_preferences, rootKey)
        preferenceManager.preferenceScreen!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                Toast.makeText(activity,preference.key,Toast.LENGTH_LONG).show()

                // Reflect the newValue to Preference?
                true
            }


    }

}