package com.chooloo.www.koler.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.chooloo.www.koler.R
import com.chooloo.www.koler.ui.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val sharedPrefFile = "kotlinsharedpreference"
    lateinit var storedVerificationId:String;
    var TAG="SHYAM"
    override fun onStart() {
        super.onStart()


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        auth = Firebase.auth
        val number=intent.getStringExtra("number");
        val otp=findViewById<EditText>(R.id.otp)
        val submit=findViewById<Button>(R.id.submit)
        submit.setOnClickListener {
            verify(storedVerificationId,otp.text.toString())
        }
        Toast.makeText(applicationContext,number, Toast.LENGTH_LONG).show()
        if(number!=null) {
           var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:$credential")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e)

                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                    } else if (e is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                    }

                    // Show a message and update the UI
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:$verificationId")
                    Toast.makeText(applicationContext,"Code Sent",Toast.LENGTH_LONG).show();
                    // Save verification ID and resending token so we can use them later

                    storedVerificationId = verificationId
//                    resendToken = token
                }
            }



            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }

    private fun verify(verificationId:String,code:String){
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(applicationContext,"DONE",Toast.LENGTH_LONG).show()
                    val user = task.result?.user
                    val sharedPreferences: SharedPreferences = this.getSharedPreferences(
                        sharedPrefFile,
                        Context.MODE_PRIVATE
                    )
                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    if (user != null) {
                        editor.putString("user_number",user.phoneNumber)

                    }
                    editor.apply()
                    val intentt= Intent(applicationContext, MainActivity::class.java)
                    intentt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intentt)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}