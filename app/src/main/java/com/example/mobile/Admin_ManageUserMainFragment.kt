package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageUserMainFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var options: FirestoreRecyclerOptions<Object_User>
    lateinit var adapter: Admin_ManageUserMainFragmentAdapter
    lateinit var registerRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var myView = inflater.inflate(R.layout.admin_list_fragment, container, false)
        myView.findViewById<TextView>(R.id.fragmentTitle).text = "User"
        registerRecycler = myView.findViewById<RecyclerView>(R.id.fragmentRecycler)
        registerRecycler.layoutManager = LinearLayoutManager(context)
        var search = myView.findViewById<SearchView>(R.id.searchbar)

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
//        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                adapter.deleteItem(
//                    viewHolder.adapterPosition,
//                    viewHolder.itemView.context,
//                    adapter.getItem(viewHolder.adapterPosition)
//                )
//            }
//        }
        loadAll()
//        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
//        itemTouchHelper.attachToRecyclerView(registerRecycler)
        return myView
    }

    fun submitSearch(s: String) {
        Log.e("mytag", "submitted")
        adapter.stopListening()
        var query = db.collection("user").whereEqualTo("admin", auth.currentUser!!.uid).orderBy("id").startAt(s)
            .endAt(s + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_User>()
            .setQuery(query, Object_User::class.java)
            .build()
        adapter = Admin_ManageUserMainFragmentAdapter(options)
        registerRecycler.adapter = adapter
        adapter.startListening()
    }

    fun loadAll() {
        var query = db.collection("user").whereEqualTo("admin", "${auth.currentUser!!.uid}").whereEqualTo("adminAccess",false)
        options = FirestoreRecyclerOptions.Builder<Object_User>()
            .setQuery(query, Object_User::class.java)
            .build()
        adapter = Admin_ManageUserMainFragmentAdapter(options)
        registerRecycler.adapter = adapter
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



