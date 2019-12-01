package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class User_GroupMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var adapter: User_GroupMainFragmentAdapter
    lateinit var options: FirestoreRecyclerOptions<Object_Group>
    lateinit var recycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var myView = inflater.inflate(R.layout.admin_list_fragment, container, false)
        var search = myView.findViewById<SearchView>(R.id.searchbar)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "Group"
        recycler = myView.findViewById<RecyclerView>(R.id.fragmentRecycler)
        recycler.layoutManager = LinearLayoutManager(context)


        loadAll()

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
            var imm = myView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            true
        }


        return myView
    }

    fun loadAll() {
        var query = db.collection("Group").whereArrayContains("user", "${auth.currentUser!!.uid}")
            .orderBy("priority", Query.Direction.ASCENDING)
        options = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(query, Object_Group::class.java)
            .build()
        adapter = User_GroupMainFragmentAdapter(options)
        recycler.adapter = adapter
        adapter.startListening()

    }

    fun submitSearch(s: String) {
        Log.e("mytag", "submitted")
        adapter.stopListening()
        var query2 =
            db.collection("Group").whereArrayContains("user", auth.currentUser!!.uid).orderBy("lowercasename").startAt(s)
                .endAt(s.replace("\\s".toRegex(), "").toLowerCase() + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(query2, Object_Group::class.java)
            .build()
        adapter = User_GroupMainFragmentAdapter(options)
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
}



