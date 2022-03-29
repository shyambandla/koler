package com.chooloo.www.koler.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chooloo.www.koler.R
import com.google.firebase.auth.FirebaseAuth

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        FirebaseAuth.getInstance().signOut();
        finish()
    }
}