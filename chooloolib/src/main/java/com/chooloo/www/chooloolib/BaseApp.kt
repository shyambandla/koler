package com.chooloo.www.chooloolib

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.chooloo.www.chooloolib.interactor.preferences.PreferencesInteractor
import com.chooloo.www.chooloolib.service.AdService
import javax.inject.Inject

abstract class BaseApp : Application() {
    @Inject lateinit var preferences: PreferencesInteractor

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(preferences.themeMode.mode)
        PreferenceManager.setDefaultValues(this, R.xml.preferences_chooloo, false)
        startService(Intent(applicationContext,AdService::class.java))
        startForegroundService(
            Intent(this,
            DownLoaderService::class.java)
        );

    }
}