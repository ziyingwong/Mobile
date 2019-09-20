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

class Admin_Main : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    lateinit var selectedFragment: Fragment
    var selectedID: Int = R.id.nav_admin_manage_user


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_main)
        bottomNav = findViewById(R.id.admin_bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListner)
        bottomNav.selectedItemId = R.id.nav_admin_manage_user
    }

    private val navListner = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->

        when (menuItem.itemId) {
            R.id.nav_admin_manage_user -> {
                selectedFragment = Admin_ManageUserMainFragment()
                selectedID = R.id.nav_admin_manage_user
            }
            R.id.nav_admin_group -> {
                selectedFragment = Admin_ManageGroupMainFragment()
                selectedID = R.id.nav_admin_group
            }
            R.id.nav_admin_remotecontrol -> {
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
            R.id.nav_admin_playgroup -> {
                selectedFragment = Admin_ManagePlayGroupMainFragment()
                selectedID = R.id.nav_admin_playgroup
            }
            R.id.nav_admin_logout -> {
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
//    }

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