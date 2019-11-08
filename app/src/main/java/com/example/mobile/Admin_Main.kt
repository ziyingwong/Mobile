package com.example.mobile

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.beust.klaxon.Klaxon
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Admin_Main : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    lateinit var selectedFragment: Fragment
    var selectedID: Int = R.id.nav_admin_manage_user
    var haveNewSceneGroup = false
    lateinit var ipAdd: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_main)
        bottomNav = findViewById(R.id.admin_bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListner)
        selectedID = intent.getIntExtra("selectedFragment", R.id.nav_admin_manage_user)
        bottomNav.selectedItemId = selectedID
        ipAdd = resources.getString(R.string.ipAdd)

        val url = "http://${ipAdd}:3000/graphql"
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, null, Response.Listener { response ->

                //get all scene stored in things factory and convert to volley response object
                Log.e("myTag", "${response}")
                val myresponse = Klaxon().parse<Object_VolleyResponse>(response.toString())
                Log.e("myTag", "things factory total : ${myresponse?.data?.boards?.total}")
                //get all scene stored in firebase
                var sceneIdfs = ArrayList<String>()
                var sceneGroup = ArrayList<ArrayList<String>>()
                db.collection("scene").whereEqualTo("admin", auth.currentUser!!.uid).get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            //put scene id and playgroup of the scene to arraylists
                            sceneGroup.add(doc.get("playgroup") as ArrayList<String>)
                            sceneIdfs.add(doc.get("id").toString())
                        }

                        //item = scene array ,  itemArr = scene id array
                        var item = myresponse!!.data!!.boards.items
                        var itemArr = ArrayList<String>()
                        Log.e("myTag", "firestore :${sceneIdfs.size}")
                        Log.e("myTag", "things factory : ${item.size}")

                        runBlocking {

                            //check if all scene in thingsfactory is stored in firestore
                            for (k in item.indices) {
                                //id from things factory not found in firestore, add to scene to firestore
                                if (item.get(k).id !in sceneIdfs) {
                                    launch {
                                        addScene(item.get(k).id, item.get(k).name,item.get(k).thumbnail)
                                    }
                                } else {
                                    launch {
                                        Log.e(
                                            "myTag",
                                            "${k + 1} scene ${item.get(k).id} exist in firestore"
                                        )
                                    }
                                }
                                itemArr.add(item.get(k).id)
                            }

                        }

                        runBlocking {

                            //check if all scene in firestore is stored in thingsboard
                            for (i in sceneIdfs.indices) {

                                //id from firestore not found in thingsfactory, delete scene in firestore
                                if (sceneIdfs.get(i) !in itemArr) {
                                    launch {
                                        removeScene(sceneIdfs.get(i))
                                    }
                                } else {
                                    launch {
                                        Log.e(
                                            "myTag",
                                            "${i + 1} scene ${sceneIdfs.get(i)} exist in things factory"
                                        )
                                    }
                                }
                            }
                        }
                    }
            },
            Response.ErrorListener { error ->
                Log.e("myTag", "Error : ${error}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val sharedPref = getSharedPreferences("pref", 0)
                var token = sharedPref.getString("token", "")
                var header = HashMap<String, String>()
                header["Cookie"] = "access_token=${token}"
                header["Origin"] = "http://${ipAdd}:3000"
                header["Referer"] = "http://${ipAdd}:3000/board-list"
                return header
            }

            override fun getBody(): ByteArray {
                var string: String =
                    "{\"query\":\"{\\n  boards(sortings: [{name: \\\"createdAt\\\", desc: true}]) {\\n    items {\\n      id\\n      name\\n        thumbnail\\n        }\\n    total\\n    }\\n}\\n\"}"
                var body: ByteArray = string.toByteArray()
                return body
            }


        }
        Singleton_Volley.getInstance(this).addToRequestQueue(jsonObjectRequest)

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
                    this.getSharedPreferences("pref",0).edit().clear().apply()
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

    suspend fun addScene(id: String, name: String,thumbnail :String) {
        Log.e("myTag", "Adding scene : ${id}")
        var group = auth.currentUser!!.uid + "newscene"
        var data = hashMapOf(
            "name" to name,
            "id" to id,
            "admin" to auth.currentUser!!.uid,
            "group" to group,
            "playgroup" to ArrayList<String>(),
            "thumbnail" to thumbnail
        )
        db.collection("scene").document(id).set(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e("myTag", "Added scene :${id}")
            } else {
                Log.e("myTag", "Fail to add scene : ${id}")
            }
        }
        runBlocking {
            if (!haveNewSceneGroup) {
                launch {
                    haveNewSceneGroup = createNewSceneGroup(group)
                }
            }
        }
    }

    suspend fun removeScene(id: String) {
        db.collection("scene").document(id).delete().addOnSuccessListener {
            Log.e("myTag", "deleted scene ${id}")
        }
    }

    suspend fun createNewSceneGroup(groupId: String): Boolean {
        var data = hashMapOf(
            "id" to groupId,
            "admin" to auth.currentUser!!.uid,
            "name" to "New Scene"
        )

        db.collection("Group").document(groupId).set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("myTag", "created new group")
            }
        return true
    }


}
