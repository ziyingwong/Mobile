package com.example.mobile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import java.lang.Error

class User_Main : AppCompatActivity() {
    // pending : bottom navigation view design
    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    lateinit var bottomNav: BottomNavigationView
    lateinit var selectedFragment: Fragment
    var selectedID: Int = R.id.nav_user_group_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_main)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            var token = it.token
            Log.e("mynoti", token)
            db.collection("user").document(auth.currentUser!!.uid).update("token", FieldValue.arrayUnion(token)).addOnSuccessListener {
                Log.e("mynoti", "updated token")
            }.addOnFailureListener { e ->
                Log.e("mynoti", e.toString())
            }
        }

        selectedID = intent.getIntExtra("selectedFragment", R.id.nav_user_group_list)
        bottomNav = findViewById(R.id.user_bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListner)
        bottomNav.selectedItemId = selectedID

    }


    private val navListner = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->

        when (menuItem.itemId) {
            R.id.nav_user_playgroup_list -> {
                selectedFragment = User_PlayGroupMainFragment()
                selectedID = R.id.nav_user_playgroup_list
            }
            R.id.nav_user_group_list -> {
                selectedFragment = User_GroupMainFragment()
                selectedID = R.id.nav_user_group_list
            }
            R.id.nav_user_remote -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Open remote control")
                builder.setMessage("You are leaving this app to open remote control app. Are you sure you want to open remote control?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    if (packageInstalled() == true) {
                        val intent =
                            packageManager.getLaunchIntentForPackage("com.google.android.tv.remote")
                        startActivity(intent)

                    } else {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tv.remote")
                        )
                        startActivity(intent)
                    }
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.cancel()
                    bottomNav.selectedItemId = selectedID

                }
                builder.show()

            }
            R.id.nav_user_logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to logout?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    this.getSharedPreferences("pref", 0).edit().clear().apply()
                    val intent = Intent(this, General_Login::class.java)
                    finishAffinity()
                    startActivity(intent)
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.cancel()
                    bottomNav.selectedItemId = selectedID

                }
                builder.show()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container, selectedFragment!!)
            .commit()
        true
    }

    fun packageInstalled(): Boolean {
        try {
            packageManager.getPackageInfo("com.google.android.tv.remote", 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}