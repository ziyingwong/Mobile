package com.example.mobile

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

class General_Webview:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)
        var webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://192.168.0.141:3000/board-viewer/07f0507e-b531-4393-a023-8938088a4182")
        val url = "http://192.168.0.141:3000/graphql"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, null, Response.Listener { response ->
                Log.e("myTag", "${response}")
            },
            Response.ErrorListener { error ->
                Log.e("myTag", "Error : ${error}")
            }
        )
        Singleton_Volley.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}