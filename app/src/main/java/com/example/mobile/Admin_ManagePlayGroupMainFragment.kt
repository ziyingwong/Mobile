package com.example.mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManagePlayGroupMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var adapter: Admin_ManagePlayGroupMainFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var query = db.collection("PlayGroup").whereEqualTo("admin", "${auth.currentUser!!.uid}")
        var options = FirestoreRecyclerOptions.Builder<Object_Playgroup>()
            .setQuery(query, Object_Playgroup::class.java)
            .build()
        var myView = inflater.inflate(R.layout.admin_list_addbutton_fragment, container, false)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "Playgroup"
        var registerRecycler = myView.findViewById<RecyclerView>(R.id.fragmentRecycler)
        adapter = Admin_ManagePlayGroupMainFragmentAdapter(options)
        registerRecycler.layoutManager = LinearLayoutManager(context)
        registerRecycler.adapter = adapter
        var button = myView.findViewById<Button>(R.id.addNewButton)
        button.text = "Add Playgroup"
        button.setOnClickListener {
            val builder = AlertDialog.Builder(view!!.context)
            builder.setTitle("Add Playgroup")
            builder.setMessage("Playgroup name : ")
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
                if (editText.text.isNullOrBlank()) {
                    Toast.makeText(view!!.context, "Playgroup name cannot be empty.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    var name = editText.text.toString()
                    var id = auth.currentUser!!.uid + name
                    var array = ArrayList<String>()
                    var info = hashMapOf(
                        "admin" to "${auth.currentUser!!.uid}",
                        "scene" to array,
                        "name" to name,
                        "id" to id
                    )
                    db.collection("PlayGroup").document(id).set(info)
                        .addOnFailureListener { it ->
                            Log.e("myTag", "${it}")
                        }
                }

            }
            builder.setNegativeButton("Cancel") { dialog, which ->
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
        if (adapter != null) {
            adapter.stopListening()
        }
    }
}