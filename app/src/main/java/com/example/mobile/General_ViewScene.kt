package com.example.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector.*
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat

class General_ViewScene : AppCompatActivity() {
    lateinit var ipAdd: String
    lateinit var mDetector: GestureDetectorCompat

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_viewscene)
        ipAdd = resources.getString(R.string.ipAdd)
        var webView = findViewById<WebView>(R.id.webView)
        var progessBar = findViewById<ProgressBar>(R.id.progressBarWebView)
        webView.settings.javaScriptEnabled = true
        var id = intent.getStringExtra("id")
        var url = "http://${ipAdd}:3000/board-viewer/${id}"
        webView.visibility = View.INVISIBLE
        progessBar.visibility = View.VISIBLE
        var controlPanel = findViewById<RelativeLayout>(R.id.controlPanel)

        mDetector = GestureDetectorCompat(this, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if (controlPanel.visibility == View.INVISIBLE) {
                    controlPanel.visibility = View.VISIBLE
                    var handler = Handler()
                    var delay: Long = 5000
                    handler.postDelayed(
                        object : Runnable {
                            override fun run() {
                                controlPanel.visibility = View.INVISIBLE
                            }
                        }, delay
                    )
                } else {
                    controlPanel.visibility = View.INVISIBLE
                }
                return true
            }


        })

        webView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return mDetector.onTouchEvent(event)
            }

        })
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






