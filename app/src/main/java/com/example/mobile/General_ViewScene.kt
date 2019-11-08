package com.example.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class General_ViewScene : AppCompatActivity() {

    lateinit var ipAdd: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_webview)
        ipAdd = resources.getString(R.string.ipAdd)
        var webView = findViewById<WebView>(R.id.webView)
        var progessBar = findViewById<ProgressBar>(R.id.progressBarWebView)
        webView.settings.javaScriptEnabled = true
        var id = intent.getStringExtra("id")
        var url = "http://${ipAdd}:3000/board-viewer/${id}"
        webView.visibility = View.INVISIBLE
        progessBar.visibility = View.VISIBLE


        var castButton = findViewById<Button>(R.id.castButton)

        webView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP-> {
                    if (castButton.visibility == View.VISIBLE) {
                        castButton.visibility = View.INVISIBLE
                    } else if (castButton.visibility == View.INVISIBLE) {
                        castButton.visibility = View.VISIBLE
                    }
                }

            }
            true
        }


        webView.setWebViewClient(object : WebViewClient() {


            override fun onPageFinished(view: WebView, url: String) {
                webView.loadUrl(
                    "javascript:document.querySelector(\"things-app\").shadowRoot.querySelector(\"header-bar\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"board-viewer-page\").shadowRoot.querySelector(\"board-viewer\").shadowRoot.querySelector(\"mwc-fab\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"footer-bar\").remove();"
                )
                progessBar.visibility = View.INVISIBLE
                webView.visibility = View.VISIBLE


            }

        })
        webView.loadUrl(url)
    }


}






