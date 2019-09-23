package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class User_PlayGroupDetailsPage:AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    lateinit var adapter : User_PlayGroupDetails_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_list_fragment)
        var name = intent.getStringExtra("name")
        var id = intent.getStringExtra("id")
        findViewById<TextView>(R.id.fragmentTitle).text = name

        var query = db.collection("scene").whereArrayContains("playgroup",id)
        var options = FirestoreRecyclerOptions.Builder<Object_Scene>()
            .setQuery(query,Object_Scene::class.java)
            .build()
        adapter = User_PlayGroupDetails_Adapter(options,id)
        var recycler = findViewById<RecyclerView>(R.id.fragmentRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

    }


    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter.stopListening()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, User_Main::class.java)
        intent.putExtra("selectedFragment",R.id.nav_user_playgroup_list)
        startActivity(intent)
        finish()

    }


}