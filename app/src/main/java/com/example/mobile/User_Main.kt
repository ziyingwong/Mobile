package com.example.mobile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.lang.Error

class User_Main : AppCompatActivity() {
    // pending : bottom navigation view design

    lateinit var bottomNav: BottomNavigationView
    lateinit var selectedFragment: Fragment
    var selectedID: Int = R.id.nav_user_group_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_main)
        bottomNav = findViewById(R.id.user_bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListner)
    }


    private val navListner = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->

        //logout

        when (menuItem.itemId) {
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
                builder.setMessage("Are you sure you want to log out?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
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