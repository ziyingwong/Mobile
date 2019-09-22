package com.example.mobile

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebViewClient

class General_Webview : AppCompatActivity() {

    var ipAdd = "10.0.2.2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)
        var webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        var id = intent.getStringExtra("id")
        var url = "http://${ipAdd}:3000/board-viewer/${id}"
        webView.setWebViewClient(object : WebViewClient() {


            override fun onPageFinished(view: WebView, url: String) {
                webView.loadUrl("javascript:document.querySelector(\"things-app\").shadowRoot.querySelector(\"header-bar\").remove();" +
                        "document.querySelector(\"things-app\").shadowRoot.querySelector(\"board-viewer-page\").shadowRoot.querySelector(\"board-viewer\").shadowRoot.querySelector(\"mwc-fab\").remove();" +
                        "document.querySelector(\"things-app\").shadowRoot.querySelector(\"footer-bar\").remove();")

            }
        })
        webView.loadUrl(url)
    }
}




