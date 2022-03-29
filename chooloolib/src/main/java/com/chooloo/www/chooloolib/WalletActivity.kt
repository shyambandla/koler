package com.chooloo.www.chooloolib

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WalletActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        var withdrawButton =findViewById<MaterialButton>(R.id.withdraw_button);
        withdrawButton.setOnClickListener {
            Toast.makeText(this,"withdraw processed",Toast.LENGTH_LONG).show()
            val alert=MaterialAlertDialogBuilder(this,R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                alert.setPositiveButton("Ok"){dialogInterface,which->
                    run {
                        dialogInterface.dismiss()
                    }
                }
                alert.setNegativeButton("Cancel"){dialog,which->
                    run {
                        dialog.dismiss()
                    }
                }
                alert.setMessage("withdrawl initiated")
                alert.setTitle("Alert")
               val al= alert.create()
            al.show()
        }
    }
}