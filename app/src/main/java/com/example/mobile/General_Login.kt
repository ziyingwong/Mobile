package com.example.mobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class General_Login : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()
    var cookiemanager = CookieManager.getInstance()
    var ipAdd = R.string.ipArr


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_login)
        var emailEditText = findViewById<EditText>(R.id.emailEditText)
        var passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        var loginButton = findViewById<Button>(R.id.loginButton)
        var forgetPasswordText = findViewById<TextView>(R.id.forgetPasswordText)
        var signUpText = findViewById<TextView>(R.id.signUpText)
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)


        //login
        loginButton.setOnClickListener {
            Log.e("myTag", "Loading : Login")
            progressBar.bringToFront()
            progressBar.visibility = View.VISIBLE
            emailEditText.isEnabled = false
            passwordEditText.isEnabled = false
            forgetPasswordText.isClickable = false
            signUpText.isClickable = false
            loginButton.isClickable = false
            var email = emailEditText.text.toString()
            var password = passwordEditText.text.toString()
            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.INVISIBLE
                emailEditText.isEnabled = true
                passwordEditText.isEnabled = true
                forgetPasswordText.isClickable = true
                signUpText.isClickable = true
                loginButton.isClickable = true
            } else {
                email = email.replace("\\s".toRegex(), "")
                password = password.replace("\\s".toRegex(), "")

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.e("myTag", "Success : Login")
                            var UID = auth.currentUser!!.uid
                            db.collection("user").document(UID).get()
                                .addOnSuccessListener { document ->

                                    //API call to login to ThingsFactory
                                    val url = "http://${ipAdd}:300/login/"
                                    val jsonObjectRequest = JsonObjectRequest(
                                        Request.Method.GET,
                                        url,
                                        null,
                                        Response.Listener { response ->
                                            var token = response.getString("access_token")
                                            Log.e("myTag", "token ${token}")
                                            val sharedPref =
                                                this.getSharedPreferences("pref", 0)
                                            val editor = sharedPref.edit()
                                            editor.putString("token", token)
                                            editor.apply()
                                            cookiemanager.setCookie(
                                                "http://${ipAdd}:3000",
                                                "access_token=${token}"
                                            )

                                            if (document.get("adminAccess") == true) {
                                                Log.e("myTag", "Access : Admin")
                                                val intent = Intent(this, Admin_Main::class.java)
                                                startActivity(intent)
                                            } else {
                                                Log.e("myTag", "Access : User")
                                                val intent = Intent(this, User_Main::class.java)
                                                startActivity(intent)
                                            }
                                        },
                                        Response.ErrorListener { error ->
                                            Log.e("myTag", "Error : ${error}")
                                            var builder = AlertDialog.Builder(this)
                                            builder.setTitle("Fail to login")
                                            builder.setMessage("Server is off. Please try again later")
                                            builder.setNeutralButton("Ok") { dialog, which ->
                                                auth.signOut()
                                                finishAffinity()
                                                startActivity(Intent(this, General_Login::class.java))
                                            }
                                            builder.show()
                                        }
                                    )
                                    Singleton_Volley.getInstance(this@General_Login)
                                        .addToRequestQueue(jsonObjectRequest)

                                }
                                .addOnFailureListener { e ->
                                    Log.e("myTag", "Failure : ${e}")
                                }
                        } else {
                            Log.e("myTag", "Failure : ${task.exception}")
                            var builder = AlertDialog.Builder(this)
                            builder.setTitle("Fail to login")
                            builder.setMessage("Please check again your email and password")
                            builder.setNeutralButton("Ok") { dialog, which ->
                                dialog.cancel()
                                progressBar.visibility = View.INVISIBLE
                                emailEditText.isEnabled = true
                                passwordEditText.isEnabled = true
                                forgetPasswordText.isClickable = true
                                signUpText.isClickable = true
                                loginButton.isClickable = true
                            }
                            builder.show()
                        }
                    }
            }
        }


        //forget password
        var forgetPwSS = SpannableString(forgetPasswordText.text.toString())
        var forgetPwClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val builder = AlertDialog.Builder(this@General_Login)
                builder.setTitle("Reset Password")
                builder.setMessage("Enter email")
                var layout = LinearLayout(this@General_Login)
                layout.orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(50, 0, 70, 0)
                var editText = EditText(this@General_Login)
                layout.addView(editText, lp)
                builder.setView(layout)
                builder.setPositiveButton("Send") { dialog, which ->
                    var email = editText.text.toString()
                    if (email.isNullOrBlank()) {
                        Toast.makeText(
                            this@General_Login,
                            "Email cannot be empty",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        email = email.replace("\\s".toRegex(), "")
                        auth.sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                Log.e("myTag", "Success : Reset password email sent")
                                var builder = AlertDialog.Builder(this@General_Login)
                                builder.setTitle("Email sent")
                                builder.setMessage("Please check your mail box and reset your password using the link in the email")
                                builder.setNeutralButton("Ok") { dialog, which ->
                                    dialog.cancel()
                                    progressBar.visibility = View.INVISIBLE
                                    emailEditText.isEnabled = true
                                    passwordEditText.isEnabled = true
                                    forgetPasswordText.isClickable = true
                                    signUpText.isClickable = true
                                    loginButton.isClickable = true
                                }
                                builder.show()
                            }.addOnFailureListener { e ->
                                Log.e("myTag", "Failure : ${e} ")
                                var builder = AlertDialog.Builder(this@General_Login)
                                builder.setTitle("Error")
                                builder.setMessage("Please check again your email address")
                                builder.setNeutralButton("Ok") { dialog, which ->
                                    dialog.cancel()
                                    progressBar.visibility = View.INVISIBLE
                                    emailEditText.isEnabled = true
                                    passwordEditText.isEnabled = true
                                    forgetPasswordText.isClickable = true
                                    signUpText.isClickable = true
                                    loginButton.isClickable = true
                                }
                                builder.show()
                            }
                    }

                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                    progressBar.visibility = View.INVISIBLE
                    emailEditText.isEnabled = true
                    passwordEditText.isEnabled = true
                    forgetPasswordText.isClickable = true
                    signUpText.isClickable = true
                    loginButton.isClickable = true
                }
                builder.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#707070")
                ds.isUnderlineText = true
            }
        }
        forgetPwSS.setSpan(
            forgetPwClickableSpan,
            0,
            forgetPwSS.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        forgetPasswordText.text = forgetPwSS
        forgetPasswordText.movementMethod = LinkMovementMethod.getInstance()


        //sign up
        var signUpSS = SpannableString(signUpText.text.toString())
        var signUpClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@General_Login, User_SignUp::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#707070")
                ds.isUnderlineText = true
            }
        }
        signUpSS.setSpan(signUpClickableSpan, 0, signUpSS.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signUpText.text = signUpSS
        signUpText.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            var emailEditText = findViewById<EditText>(R.id.emailEditText)
            var passwordEditText = findViewById<EditText>(R.id.passwordEditText)
            var loginButton = findViewById<Button>(R.id.loginButton)
            var forgetPasswordText = findViewById<TextView>(R.id.forgetPasswordText)
            var signUpText = findViewById<TextView>(R.id.signUpText)
            var progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.bringToFront()
            progressBar.visibility = View.VISIBLE
            emailEditText.isEnabled = false
            passwordEditText.isEnabled = false
            forgetPasswordText.isClickable = false
            signUpText.isClickable = false
            loginButton.isClickable = false
            val UID = auth.currentUser!!.uid
            val sharedPref =
                this.getSharedPreferences("pref", 0)
            var token = sharedPref.getString("token", "")
            if (token.isNullOrBlank()) {
                auth.signOut()
                finishAffinity()
                startActivity(Intent(this, General_Login::class.java))
            } else {
                cookiemanager.setCookie(
                    "http://${ipAdd}:3000",
                    "access_token=${token}"
                )
                Log.e("myTag", "token : ${token}")
                db.collection("user").document(UID).get()
                    .addOnSuccessListener { document ->
                        if (document.get("adminAccess") == true) {
                            Log.e("myTag", "Access : Admin")
                            val intent = Intent(this, Admin_Main::class.java)
                            startActivity(intent)
                        } else {
                            Log.e("myTag", "Access : User")
                            val intent = Intent(this, User_Main::class.java)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("myTag", "Failure : ${e}")
                    }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}
