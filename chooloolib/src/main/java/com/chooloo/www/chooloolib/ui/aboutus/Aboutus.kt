package com.chooloo.www.chooloolib.ui.aboutus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.chooloo.www.chooloolib.R

class Aboutus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        var webview=findViewById<WebView>(R.id.webview)
        webview.loadUrl("file:///android_asset/about_us.html")

    }
}