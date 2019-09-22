package com.example.mobile

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageGroupDetailsPage:AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    lateinit var adapter : Admin_ManageGroupDetails_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_list_addbutton_fragment)
        var name = intent.getStringExtra("name")
        var id = intent.getStringExtra("id")
        findViewById<TextView>(R.id.fragmentTitle).text = name

        var query = db.collection("scene").whereEqualTo("group",id)
        var options = FirestoreRecyclerOptions.Builder<Object_Scene>()
            .setQuery(query,Object_Scene::class.java)
            .build()
        adapter = Admin_ManageGroupDetails_Adapter(options)
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
        super.onBackPressed()
        finish()
    }


}