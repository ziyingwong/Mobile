package com.example.mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class User_GroupMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var adapter: User_GroupMainFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var query = db.collection("Group").whereArrayContains("user", "${auth.currentUser!!.uid}")
        var options = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(query, Object_Group::class.java)
            .build()
        var myView = inflater.inflate(R.layout.admin_list_fragment, container, false)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "Group"
        var recycler = myView.findViewById<RecyclerView>(R.id.fragmentRecycler)
        adapter = User_GroupMainFragmentAdapter(options)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        return myView
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



