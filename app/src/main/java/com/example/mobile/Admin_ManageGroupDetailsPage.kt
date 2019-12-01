package com.example.mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Admin_ManageGroupDetailsPage : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    lateinit var adapter: Admin_ManageGroupDetails_Adapter
    lateinit var options: FirestoreRecyclerOptions<Object_Scene>
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_list_addbutton_fragment)
        var name = intent.getStringExtra("name")
        var id = intent.getStringExtra("id")
        recycler = findViewById<RecyclerView>(R.id.fragmentRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        findViewById<TextView>(R.id.fragmentTitle).text = name

        //search
        var search = findViewById<SearchView>(R.id.searchbar)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrBlank()) {
                    adapter.stopListening()
                    loadAll(id)
                    true
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!p0.isNullOrBlank()) {
                    submitSearch(p0, id)
                }
                return false
            }
        })
        search.setOnCloseListener {
            search.clearFocus()
            var imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
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
                adapter.changeGroup(
                    viewHolder.itemView.context,
                    adapter.getItem(viewHolder.adapterPosition)
                )
            }
        }
        loadAll(id)
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(recycler)

        //add scene
        var button = findViewById<Button>(R.id.addNewButton)
        button.text = "Add Scene"
        button.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            db.collection("scene").whereEqualTo("admin", auth.currentUser!!.uid).get()
                .addOnSuccessListener { sceneDoc ->
                    if (sceneDoc.documents.size > 0) {
                        var selectedArray = ArrayList<Object_Scene>()
                        var array = ArrayList<Object_Scene>()
                        for (doc in sceneDoc.documents) {
                            if (!doc.get("group")!!.equals(id)) {
                                var sceneToAdd = Object_Scene()
                                sceneToAdd.id = doc.id
                                sceneToAdd.group = doc.get("group").toString()
                                sceneToAdd.name = doc.get("name").toString()
                                sceneToAdd.thumbnail = doc.get("thumbnail").toString()
                                array.add(sceneToAdd)
                            }
                        }
                        var layout = LinearLayout(this)
                        layout.orientation = LinearLayout.VERTICAL
                        val lp = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            600
                        )
                        lp.setMargins(50, 50, 70, 0)
                        var radioLayout = LayoutInflater.from(this).inflate(R.layout.other_radiobuttons, layout, false)
                        var radioGroup = radioLayout.findViewById<RadioGroup>(R.id.radioGroup)
                        for (sceneObject in array) {
                            val checkBox = CheckBox(this)
                            checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                                override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                    if (p1) {
                                        selectedArray.add(sceneObject)
                                    } else {
                                        selectedArray.remove(sceneObject)
                                    }
                                }
                            })
                            checkBox.id = array.indexOf(sceneObject)
                            checkBox.text = sceneObject.name
                            radioGroup.addView(checkBox)

                        }
                        layout.addView(radioLayout, lp)
                        alertDialog.setView(layout)
                        alertDialog.setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        alertDialog.setPositiveButton("Add") { dialog, which ->
                            db.runBatch { writeBatch ->
                                for (selectedScene in selectedArray) {
                                    Log.e("mytag", selectedScene.name)
                                    var data = hashMapOf(
                                        "group" to id
                                    )
                                    writeBatch.set(
                                        db.collection("scene").document(selectedScene.id),
                                        data,
                                        SetOptions.merge()
                                    )
                                }
                                writeBatch.update(
                                    db.collection("Group").document(id),
                                    "imageUrl",
                                    selectedArray.get(0).thumbnail
                                )
                            }.addOnSuccessListener {
                                Log.e("mytag", "updated ${selectedArray.size} scene")
                            }.addOnFailureListener { E ->
                                Log.e("mytag", "$E")
                            }

                        }
                        alertDialog.show()
                    }
                }.addOnFailureListener { e ->
                    Log.e("mytag", "$e")
                }
        }
    }

    fun submitSearch(s: String, id: String) {
        Log.e("mytag", "submitted")
        adapter.stopListening()
        var query = db.collection("scene").whereEqualTo("group", id).orderBy("lowercasename").startAt(s)
            .endAt(s.replace("\\s".toRegex(), "").toLowerCase() + "\uf8ff")
        options = FirestoreRecyclerOptions.Builder<Object_Scene>()
            .setQuery(query, Object_Scene::class.java)
            .build()
        adapter = Admin_ManageGroupDetails_Adapter(options,id)
        recycler.adapter = adapter
        adapter.startListening()
    }

    fun loadAll(id: String) {
        var query = db.collection("scene").whereEqualTo("group", id)
        options = FirestoreRecyclerOptions.Builder<Object_Scene>()
            .setQuery(query, Object_Scene::class.java)
            .build()
        adapter = Admin_ManageGroupDetails_Adapter(options,id)
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