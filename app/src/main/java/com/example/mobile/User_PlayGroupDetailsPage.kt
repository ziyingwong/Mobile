package com.example.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class User_PlayGroupDetailsPage : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    lateinit var adapter: User_PlayGroupDetails_Adapter
    lateinit var recycler: RecyclerView
    lateinit var options: FirestoreRecyclerOptions<Object_Scene>
    lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_list_fragment)
        var name = intent.getStringExtra("name")
        id = intent.getStringExtra("id")

        var search = findViewById<SearchView>(R.id.searchbar)
        recycler = findViewById(R.id.fragmentRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        findViewById<TextView>(R.id.fragmentTitle).text = name

        loadAll()
//        var query = db.collection("scene").whereArrayContains("playgroup", id)
//        options = FirestoreRecyclerOptions.Builder<Object_Scene>()
//            .setQuery(query, Object_Scene::class.java)
//            .build()
//        adapter = User_PlayGroupDetails_Adapter(options, id)
//        recycler.adapter = adapter

        //search
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrBlank()) {
                    adapter.stopListening()
                    loadAll()
                    true
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!p0.isNullOrBlank()) {
                    submitSearch(p0)
                }
                return false
            }
        })
        search.setOnCloseListener {
            search.clearFocus()
            var imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search.windowToken, 0)
            true
        }

    }

    fun loadAll() {
        var query = db.collection("scene").whereArrayContains("playgroup", id)
        options = FirestoreRecyclerOptions.Builder<Object_Scene>()
            .setQuery(query, Object_Scene::class.java)
            .build()
        adapter = User_PlayGroupDetails_Adapter(options,id)
        recycler.adapter = adapter
        adapter.startListening()

    }

    fun submitSearch(s: String) {
        Log.e("mytag", "submitted")
        adapter.stopListening()
        var query2 = db.collection("scene").whereArrayContains("playgroup", id).orderBy("lowercasename")
            .startAt(s)
            .endAt(s.replace("\\s".toRegex(), "").toLowerCase() + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_Scene>()
            .setQuery(query2, Object_Scene::class.java)
            .build()
        adapter = User_PlayGroupDetails_Adapter(options,id)
        recycler.adapter = adapter
        adapter.startListening()
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
        intent.putExtra("selectedFragment", R.id.nav_user_playgroup_list)
        startActivity(intent)
        finish()

    }


}