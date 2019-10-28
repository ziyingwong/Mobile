package com.example.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity


class User_SignUp_Details : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_empty_webview)
        var url = intent.getStringExtra("link")
        var webView = findViewById<WebView>(R.id.emptyWebView)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.loadUrl(url)
    }
}

class WebAppInterface(private val mContext: Context) {


    @JavascriptInterface
    fun completeRegistration() {
        var intent = Intent(mContext, General_Login::class.java)
        Toast.makeText(mContext, "Registration success", Toast.LENGTH_LONG).show()

        val thread = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(1000) // As I am using LENGTH_LONG in Toast
                    mContext.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        thread.start()
    }

    @JavascriptInterface
    fun failRegistration(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show()
    }


}