package com.example.mobile

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.beust.klaxon.Klaxon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import org.json.JSONStringer

class General_Webview : AppCompatActivity() {

    var ipAdd = "10.0.2.2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)
        var webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://${ipAdd}:3000/board-viewer/${DataContainer_Scene.id}")

    }
}



