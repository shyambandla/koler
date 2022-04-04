package com.chooloo.www.chooloolib.ui.ads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chooloo.www.chooloolib.R

class SelectPreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_preferences)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, SettingsFragment())
            .commit()
    }
    }
