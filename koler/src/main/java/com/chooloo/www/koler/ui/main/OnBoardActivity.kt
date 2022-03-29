package com.chooloo.www.koler.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chooloo.www.koler.R
import com.chooloo.www.koler.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class OnBoardActivity : AppCompatActivity() {
    private lateinit var fireAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board)
        fireAuth=FirebaseAuth.getInstance()
        if(fireAuth.currentUser==null){
            val intentt= Intent(applicationContext,LoginActivity::class.java)
            intentt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentt)
        }else{
            val intentt= Intent(applicationContext,MainActivity::class.java)
            intentt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentt)
        }
    }
}