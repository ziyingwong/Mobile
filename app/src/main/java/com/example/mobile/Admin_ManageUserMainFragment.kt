package com.example.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageUserMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var adapter: Admin_ManageUserMainFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var query = db.collection("user").whereEqualTo("admin", "${auth.currentUser!!.uid}")
        var options = FirestoreRecyclerOptions.Builder<Object_User>()
            .setQuery(query, Object_User::class.java)
            .build()
        var myView = inflater.inflate(R.layout.admin_list_fragment,container,false)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "User"
        var registerRecycler = myView.findViewById<RecyclerView>(R.id.fragmentRecycler)
        adapter= Admin_ManageUserMainFragmentAdapter(options)
        registerRecycler.layoutManager = LinearLayoutManager(context)
        registerRecycler.adapter = adapter
        return myView
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if(adapter!=null){
            adapter.stopListening()
        }
    }
}



