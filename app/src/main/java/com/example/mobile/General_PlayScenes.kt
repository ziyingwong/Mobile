package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class General_PlayScenes : AppCompatActivity() {
    var ipAdd = "10.0.2.2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_webview)
        var webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        var list = intent.getStringArrayListExtra("list")
        var selected = intent.getIntExtra("selected",0)
        var url = "http://${ipAdd}:3000/board-viewer/"

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {

                webView.loadUrl(
                    "javascript:document.querySelector(\"things-app\").shadowRoot.querySelector(\"header-bar\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"board-viewer-page\").shadowRoot.querySelector(\"board-viewer\").shadowRoot.querySelector(\"mwc-fab\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"footer-bar\").remove();"
                )


            }
        })
        if (selected == list.size) {
            selected = 0
        }
        webView.loadUrl(url + list.get(selected))

        var handler = Handler()
        var delay: Long = 20000
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    val intent = Intent(this@General_PlayScenes, General_PlayScenes::class.java)
                    intent.putExtra("list", list)
                    intent.putExtra("selected", selected+1)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    startActivity(intent)
                }
            }, delay
        )

    }

    override fun onBackPressed() {
        val intent = Intent(this@General_PlayScenes, Admin_Main::class.java)
        intent.putExtra("selectedFragment",R.id.nav_admin_playgroup)
        startActivity(intent)
        finish()

    }

}