package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Admin_ManageGroupMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var adapter: Admin_ManageGroupMainFragmentAdapter
    lateinit var recycler: RecyclerView
    lateinit var options: FirestoreRecyclerOptions<Object_Group>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var myView = inflater.inflate(R.layout.admin_list_addbutton_fragment, container, false)
        var search = myView.findViewById<SearchView>(R.id.searchbar)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "Group"

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

        //recycler view
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
                    viewHolder.itemView.context,
                    adapter.getItem(viewHolder.adapterPosition)
                )
            }
        }
        loadAll()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(recycler)


        //add new group
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
                if (editText.text.isNullOrBlank()) {
                    Toast.makeText(view!!.context, "Group name cannot be empty.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    var name = editText.text.toString()

                    var lowercasename = name.replace("\\s".toRegex(), "").toLowerCase()
                    var id = auth.currentUser!!.uid +  name.replace("\\s".toRegex(), "")
                    var array = ArrayList<String>()
                    array.add(auth.currentUser!!.uid)
                    var info = hashMapOf(
                        "admin" to "${auth.currentUser!!.uid}",
                        "user" to array,
                        "name" to name,
                        "id" to id,
                        "lowercasename" to lowercasename,
                        "imageUrl" to "",
                        "priority" to 1
                    )
                    db.collection("Group").document(id).set(info)
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
            db.collection("Group").whereEqualTo("admin", auth.currentUser!!.uid).orderBy("lowercasename").startAt(s)
                .endAt(s.replace("\\s".toRegex(), "").toLowerCase() + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(query2, Object_Group::class.java)
            .build()
        adapter = Admin_ManageGroupMainFragmentAdapter(options)
        recycler.adapter = adapter
        adapter.startListening()
    }

    fun loadAll() {
        var query = db.collection("Group").whereEqualTo("admin", "${auth.currentUser!!.uid}")
            .orderBy("priority", Query.Direction.ASCENDING)
        options = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(query, Object_Group::class.java)
            .build()
        adapter = Admin_ManageGroupMainFragmentAdapter(options)
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



