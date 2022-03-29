package com.chooloo.www.chooloolib.ui.ads

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.chooloo.www.chooloolib.R
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.ad_preferences, rootKey)
    }
}