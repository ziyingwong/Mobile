package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class User_SignUp : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    lateinit var adapter: User_SignUp_Adapter
    lateinit var options: FirestoreRecyclerOptions<Object_company>
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_list_fragment)
        var title = findViewById<TextView>(R.id.fragmentTitle)
        var search = findViewById<SearchView>(R.id.searchbar)

        title.text = "Select Company"
//        var query = db.collection("companyList").orderBy("name")
//        var options = FirestoreRecyclerOptions.Builder<Object_company>()
//            .setQuery(query, Object_company::class.java)
//            .build()
//        adapter = User_SignUp_Adapter(options)
        recycler = findViewById<RecyclerView>(R.id.fragmentRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
//        recycler.adapter = adapter
        loadAll()

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
        var query = db.collection("companyList")
        options = FirestoreRecyclerOptions.Builder<Object_company>()
            .setQuery(query, Object_company::class.java)
            .build()
        adapter = User_SignUp_Adapter(options)
        recycler.adapter = adapter
        adapter.startListening()

    }

    fun submitSearch(s: String) {
        Log.e("mytag", "submitted")
        adapter.stopListening()
        var query2 = db.collection("companyList")
                .orderBy("lowercasename")
                .startAt(s)
                .endAt(s.replace("\\s".toRegex(), "").toLowerCase() + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_company>()
            .setQuery(query2, Object_company::class.java)
            .build()
        adapter = User_SignUp_Adapter(options)
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
        super.onBackPressed()
        finish()
    }
}