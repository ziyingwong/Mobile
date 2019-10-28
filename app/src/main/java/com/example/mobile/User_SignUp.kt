package com.example.mobile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class User_SignUp : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    lateinit var adapter : User_SignUp_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_list_fragment)
        var title = findViewById<TextView>(R.id.fragmentTitle)
        title.text = "Select Company"
        var query = db.collection("companyList").orderBy("name")
        var options = FirestoreRecyclerOptions.Builder<Object_company>()
            .setQuery(query, Object_company::class.java)
            .build()
        adapter = User_SignUp_Adapter(options)
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