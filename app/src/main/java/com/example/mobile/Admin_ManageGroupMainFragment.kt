package com.example.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageGroupMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var adapter: Admin_ManageUserMainFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var query = db.collection("Group").whereEqualTo("admin", "${auth.currentUser!!.uid}")
        var options = FirestoreRecyclerOptions.Builder<Object_User>()
            .setQuery(query, Object_User::class.java)
            .build()
        var myView = inflater.inflate(R.layout.admin_list_addbutton_fragment,container,false)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "Group"
        var registerRecycler = myView.findViewById<RecyclerView>(R.id.fragmentRecycler)
        adapter= Admin_ManageUserMainFragmentAdapter(options)
        registerRecycler.layoutManager = LinearLayoutManager(context)
        registerRecycler.adapter = adapter
        var button = myView.findViewById<Button>(R.id.addNewButton)
        button.text = "Add Group"
        button.setOnClickListener {
            val builder = AlertDialog.Builder(view!!.context)
            builder.setTitle("Add Group")
            builder.setMessage("Group name : ")
            var layout = LinearLayout(view!!.context)
            layout.orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(50, 0, 70, 0)
            var editText = EditText(view!!.context)
            layout.addView(editText, lp)
            builder.setView(layout)
            builder.setPositiveButton("Add") { dialog, which ->
                //add new Group
            }
            builder.setNegativeButton("Cancel"){ dialog, which ->
                dialog.cancel()
            }

            builder.show()
        }
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



