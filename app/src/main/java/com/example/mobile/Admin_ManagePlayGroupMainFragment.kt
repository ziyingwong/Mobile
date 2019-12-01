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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Admin_ManagePlayGroupMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var recycler: RecyclerView
    lateinit var adapter: Admin_ManagePlayGroupMainFragmentAdapter
    lateinit var options: FirestoreRecyclerOptions<Object_Playgroup>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var myView = inflater.inflate(R.layout.admin_list_addbutton_fragment, container, false)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "Playgroup"
        var search = myView.findViewById<SearchView>(R.id.searchbar)

        recycler = myView.findViewById(R.id.fragmentRecycler)
        recycler.layoutManager = LinearLayoutManager(context)

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

        //recycle view
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.deleteItem(
                    viewHolder.adapterPosition,
                    viewHolder.itemView.context
                )
            }
        }
        loadAll()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(recycler)


        //add playgroup
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
                    Toast.makeText(
                        view!!.context,
                        "Playgroup name cannot be empty.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    var name = editText.text.toString()
                    var id = auth.currentUser!!.uid + name
                    var array = ArrayList<String>()
                    array.add(auth.currentUser!!.uid)
                    var info = hashMapOf(
                        "admin" to "${auth.currentUser!!.uid}",
                        "user" to array,
                        "name" to name,
                        "id" to id,
                        "imageUrl" to "",
                        "lowercasename" to name.replace("\\s".toRegex(), "").toLowerCase()
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

    fun submitSearch(s: String) {
        Log.e("mytag", "submitted")
        adapter.stopListening()
        var query2 =
            db.collection("PlayGroup").whereEqualTo("admin", auth.currentUser!!.uid).orderBy("lowercasename").startAt(s)
                .endAt(s.replace("\\s".toRegex(), "").toLowerCase() + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_Playgroup>()
            .setQuery(query2, Object_Playgroup::class.java)
            .build()
        adapter = Admin_ManagePlayGroupMainFragmentAdapter(options)
        recycler.adapter = adapter
        adapter.startListening()
    }

    fun loadAll() {
        var query2 = db.collection("PlayGroup").whereEqualTo("admin", "${auth.currentUser!!.uid}")
        options = FirestoreRecyclerOptions.Builder<Object_Playgroup>()
            .setQuery(query2, Object_Playgroup::class.java)
            .build()
        adapter = Admin_ManagePlayGroupMainFragmentAdapter(options)
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