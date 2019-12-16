package com.example.mobile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import android.widget.NumberPicker
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.AppIndex
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.appindexing.Action.TYPE_VIEW


class General_PlayScenes : AppCompatActivity() {

    lateinit var ipAdd: String
    lateinit var mDetector: GestureDetectorCompat
    lateinit var countDown: CountDownTimer
    var counter = DataContainer_Other.timeInterval.toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_playscene)
        ipAdd = resources.getString(R.string.ipAdd)
        var webView = findViewById<WebView>(R.id.webView)
        var progressBar = findViewById<ProgressBar>(R.id.progressBarWebView)
        var list = intent.getStringArrayListExtra("list")
        var selected = intent.getIntExtra("selected", 0)

        var url = "https://board.opa-x.com/domain/demo/board-viewer/"
//        var url = "http://${ipAdd}:3000/board-viewer/"
        var controlPanel = findViewById<RelativeLayout>(R.id.controlPanel)
        var timer = findViewById<ImageView>(R.id.scene_timer)
        var previous = findViewById<ImageView>(R.id.scene_previous)
        var next = findViewById<ImageView>(R.id.scene_next)
        var pause = findViewById<ImageView>(R.id.scene_pause)
        var cast = findViewById<ImageView>(R.id.scene_cast)
        var isRunning = false
        webView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        cast.setOnClickListener {
            startActivity(Intent("android.settings.CAST_SETTINGS"))
        }

        //set timer
        countDown = object : CountDownTimer(counter, 1000) {
            override fun onFinish() {
                isRunning = false
                val intent = Intent(this@General_PlayScenes, General_PlayScenes::class.java)
                intent.putExtra("list", list)
                intent.putExtra("selected", selected + 1)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun onTick(millisUntilFinished: Long) {
                isRunning = true
                counter -= 1000
            }
        }

        //set onClick listeners
        next.setOnClickListener {
            countDown.cancel()
            countDown.onFinish()

        }

        previous.setOnClickListener {
            countDown.cancel()
            val intent = Intent(this@General_PlayScenes, General_PlayScenes::class.java)
            intent.putExtra("list", list)
            intent.putExtra("selected", selected - 1)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        pause.setOnClickListener {
            if (isRunning) {
                pause.setImageResource(R.drawable.play)
                countDown.cancel()
                isRunning = false
            } else {
                pause.setImageResource(R.drawable.pause)
                countDown = object : CountDownTimer(counter.toLong(), 1000) {
                    override fun onFinish() {
                        isRunning = false
                        val intent = Intent(this@General_PlayScenes, General_PlayScenes::class.java)
                        intent.putExtra("list", list)
                        intent.putExtra("selected", selected + 1)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        isRunning = true
                        counter -= 1000
                    }
                }
                countDown.start()
            }
        }

        timer.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            var mView = layoutInflater.inflate(R.layout.other_spinner, null)
            builder.setTitle("Set time interval")
            var minPicker = mView.findViewById<NumberPicker>(R.id.minutes)
            var sPicker = mView.findViewById<NumberPicker>(R.id.seconds)
            var mValue = arrayOf("0", "1", "2", "3", "4")
            var sValueShort = arrayOf("30", "35", "40", "45", "50", "55")
            var sValueLong =
                arrayOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
            minPicker.maxValue = 4
            minPicker.minValue = 0
            sPicker.maxValue = 5
            sPicker.minValue = 0
            minPicker.displayedValues = arrayOf("0", "1", "2", "3", "4")
            sPicker.displayedValues = sValueShort
            minPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
                if (newVal == 0) {
                    if (sPicker.value < 6) {
                        sPicker.value = 0
                    } else {
                        sPicker.value = sPicker.value - 6
                    }
                    sPicker.maxValue = 5
                    sPicker.displayedValues = sValueShort

                } else if (oldVal == 0) {
                    sPicker.displayedValues = sValueLong
                    sPicker.maxValue = 11
                    sPicker.value = sPicker.value + 6
                }
            })

            mView.minimumHeight = 600
            builder.setView(mView)
            builder.setPositiveButton("Save") { dialog, which ->
                Log.e("myTag", "time interval saved")
                var second = sPicker.value
                if (minPicker.value == 0) {
                    second = sValueShort[second].toInt()
                } else {
                    second = sValueLong[second].toInt()
                }
                var newTime = mValue[minPicker.value].toInt() * 60 + second
                DataContainer_Other.timeInterval = newTime * 1000
                dialog.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                Log.e("myTag", "cancelled")
                dialog.dismiss()
            }

            builder.show()
        }

        webView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return mDetector.onTouchEvent(event)
            }

        })

        mDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
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
        //webView setting
        webView.settings.javaScriptEnabled = true
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {

                view.loadUrl(
                    "javascript:document.querySelector(\"things-app\").shadowRoot.querySelector(\"header-bar\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"main\").querySelector(\"app-board-viewer-page\").shadowRoot.querySelector(\"board-viewer\").shadowRoot.querySelector(\"mwc-fab\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"footer-bar\").remove();" +
                            "document.querySelector(\"things-app\").shadowRoot.querySelector(\"snack-bar\").remove();"

                )
                progressBar.visibility = View.INVISIBLE
                webView.visibility = View.VISIBLE


            }
        })


        //start loading scene
        if (selected == list.size) {
            selected = 0
        } else if (selected < 0) {
            selected = list.size - 1
        }
        webView.loadUrl(url + list.get(selected))
        countDown.start()

    }


    override fun onBackPressed() {
        super.onBackPressed()
        countDown.cancel()
    }

}