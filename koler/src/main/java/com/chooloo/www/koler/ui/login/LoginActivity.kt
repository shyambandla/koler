package com.chooloo.www.koler.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.chooloo.www.koler.R
import com.hbb20.CountryCodePicker

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val verify=findViewById<Button>(R.id.next)
        val countryCodePicker=findViewById<CountryCodePicker>(R.id.ccp)
       val number = findViewById<EditText>(R.id.editText_carrierNumber)
        countryCodePicker.registerCarrierNumberEditText(number)
        verify.setOnClickListener {
            val intent= Intent(applicationContext,VerificationActivity::class.java);
            val fullnumber=countryCodePicker.fullNumberWithPlus
            intent.putExtra("number",fullnumber)

            startActivity(intent);
        }
    }
}