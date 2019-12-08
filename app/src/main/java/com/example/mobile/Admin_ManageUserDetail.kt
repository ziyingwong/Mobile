package com.example.mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageUserDetail : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    lateinit var uid: String
    lateinit var recycler: RecyclerView
    lateinit var groupAdapter: Admin_ManageUserGroupAdapter
    lateinit var playGroupAdapter: Admin_ManageUserPlayGroupAdapter
    lateinit var notificationAdapter: Admin_ManageUserNotificationAdapter
    lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_manage_user_detail)
        uid = intent.getStringExtra("uid")
        var id = intent.getStringExtra("id")
        var tab = findViewById<TabLayout>(R.id.tab)
        var title = findViewById<TextView>(R.id.idText)
        addButton = findViewById(R.id.addNewButton)
        addButton.text = "Add Group"
        title.text = id


        recycler = findViewById(R.id.userRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.e("mytag", tab.selectedTabPosition.toString())
                when (tab.selectedTabPosition) {
                    0 -> {
                        groupAdapter.deleteItem(
                            viewHolder.adapterPosition,
                            uid
                        )
                        loadRecyclerView("Group")

                    }
                    1 -> {
                        playGroupAdapter.deleteItem(
                            viewHolder.adapterPosition,
                            uid
                        )
                        loadRecyclerView("PlayGroup")
                    }
                    2 -> {
                        notificationAdapter.deleteItem(
                            viewHolder.adapterPosition, uid
                        )
                        loadRecyclerView("Notification")

                    }

                }
            }
        }
        loadRecyclerView("Group")
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(recycler)





        tab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {
                    Log.e("mytag", "reselected")

//                    when (p0!!.position) {
//                        0 -> {
//                            addButton.text = "Add Group"
//                            recycler.adapter = groupAdapter
//                            groupAdapter.startListening()
//                        }
//                        1 -> {
//                            addButton.text = "Add Play Group"
//                            recycler.adapter = playGroupAdapter
//                            playGroupAdapter.startListening()
//
//                        }
//                        2 -> {
//                            addButton.text = "Add Subscription"
//                            recycler.adapter = notificationAdapter
//                            notificationAdapter.startListening()
//                        }
//                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabSelected(p0: TabLayout.Tab?) {
                    Log.e("mytag", "selected")
                    when (p0!!.position) {
                        0 -> {
                            addButton.text = "Add Group"
                            loadRecyclerView("Group")
                        }
                        1 -> {
                            addButton.text = "Add Playgroup"
                            loadRecyclerView("PlayGroup")
                        }
                        2 -> {
                            addButton.text = "Add Subscription"
                            loadRecyclerView("Notification")
                        }
                    }
                }

            })

    }

    fun loadRecyclerView(about: String) {
        var query = db.collection(about).whereArrayContains("user", uid)
        when (about) {
            "Group" -> {
                var options =
                    FirestoreRecyclerOptions.Builder<Object_Group>().setQuery(query, Object_Group::class.java).build()
                groupAdapter = Admin_ManageUserGroupAdapter(options)
                recycler.adapter = groupAdapter
                groupAdapter.startListening()
            }
            "PlayGroup" -> {
                var options =
                    FirestoreRecyclerOptions.Builder<Object_Playgroup>().setQuery(query, Object_Playgroup::class.java)
                        .build()
                playGroupAdapter = Admin_ManageUserPlayGroupAdapter(options)
                recycler.adapter = playGroupAdapter
                playGroupAdapter.startListening()
            }
            "Notification" -> {
                var options =
                    FirestoreRecyclerOptions.Builder<Object_Notification>()
                        .setQuery(query, Object_Notification::class.java).build()
                notificationAdapter = Admin_ManageUserNotificationAdapter(options)
                recycler.adapter = notificationAdapter
                notificationAdapter.startListening()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (this::notificationAdapter.isInitialized) {
            notificationAdapter.startListening()
        }
        if (this::playGroupAdapter.isInitialized) {
            playGroupAdapter.startListening()

        }
        if (this::groupAdapter.isInitialized) {
            groupAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (this::notificationAdapter.isInitialized) {
            notificationAdapter.stopListening()
        }
        if (this::playGroupAdapter.isInitialized) {
            playGroupAdapter.stopListening()

        }
        if (this::groupAdapter.isInitialized) {
            groupAdapter.stopListening()
        }

    }

    fun onClick(v: View) {
        var v = v as Button
        when (v.text) {
            "Add Group" -> {
                Log.e("mytag", "add group")
                addGroup()
            }
            "Add Play Group" -> {
                Log.e("mytag", "add playgroup")
                addPlayGroup()

            }
            "Add Subscription" -> {
                Log.e("mytag", "add noti")
                addSubscription()

            }
        }

    }

    fun addGroup() {
        val alertDialog = AlertDialog.Builder(this)
        db.collection("Group").whereEqualTo("admin", auth.currentUser!!.uid).get()
            .addOnSuccessListener { group ->
                if (group.documents.size > 0) {
                    var selectedArray = ArrayList<Object_Group>()
                    var array = ArrayList<Object_Group>()
                    for (groupObject in group.documents) {
                        if (uid !in groupObject.get("user") as ArrayList<String>) {
                            array.add(groupObject.toObject(Object_Group::class.java)!!) //all group available
                        }
                    }
                    var layout = LinearLayout(this)
                    layout.orientation = LinearLayout.VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        600
                    )
                    lp.setMargins(50, 50, 70, 0)
                    var radioLayout =
                        LayoutInflater.from(this).inflate(R.layout.other_radiobuttons, layout, false)
                    var radioGroup = radioLayout.findViewById<RadioGroup>(R.id.radioGroup)
                    for (groupObject in array) {
                        val checkBox = CheckBox(this)
                        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                if (p1) {
                                    selectedArray.add(groupObject)
                                } else {
                                    selectedArray.remove(groupObject)
                                }
                            }
                        })
                        checkBox.id = array.indexOf(groupObject)
                        checkBox.text = groupObject.name
                        radioGroup.addView(checkBox)

                    }
                    layout.addView(radioLayout, lp)
                    alertDialog.setView(layout)
                    alertDialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Add") { dialog, which ->
                        db.runBatch { writeBatch ->
                            for (selectedGroup in selectedArray) {
                                Log.e("mytag", selectedGroup.name)
                                writeBatch.update(
                                    db.collection("Group").document(selectedGroup.id),
                                    "user",
                                    FieldValue.arrayUnion(
                                        uid
                                    )
                                )
                            }

                        }.addOnSuccessListener {
                            Log.e("mytag", "updated ${selectedArray.size} Group")
                        }.addOnFailureListener { E ->
                            Log.e("mytag", "$E")
                        }
                        loadRecyclerView("Group")
                    }
                    alertDialog.show()
                }
            }.addOnFailureListener { e ->
                Log.e("mytag", "$e")
            }
    }

    fun addPlayGroup() {
        val alertDialog = AlertDialog.Builder(this)
        db.collection("PlayGroup").whereEqualTo("admin", auth.currentUser!!.uid).get()
            .addOnSuccessListener { playgroup ->
                if (playgroup.documents.size > 0) {
                    var selectedArray = ArrayList<Object_Playgroup>()
                    var array = ArrayList<Object_Playgroup>()
                    for (playgroupObject in playgroup.documents) {
                        if (uid !in playgroupObject.get("user") as ArrayList<String>) {
                            array.add(playgroupObject.toObject(Object_Playgroup::class.java)!!) //all group available
                        }
                    }
                    var layout = LinearLayout(this)
                    layout.orientation = LinearLayout.VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        600
                    )
                    lp.setMargins(50, 50, 70, 0)
                    var radioLayout =
                        LayoutInflater.from(this).inflate(R.layout.other_radiobuttons, layout, false)
                    var radioGroup = radioLayout.findViewById<RadioGroup>(R.id.radioGroup)
                    for (playgroupObject in array) {
                        val checkBox = CheckBox(this)
                        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                if (p1) {
                                    selectedArray.add(playgroupObject)
                                } else {
                                    selectedArray.remove(playgroupObject)
                                }
                            }
                        })
                        checkBox.id = array.indexOf(playgroupObject)
                        checkBox.text = playgroupObject.name
                        radioGroup.addView(checkBox)

                    }
                    layout.addView(radioLayout, lp)
                    alertDialog.setView(layout)
                    alertDialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Add") { dialog, which ->
                        db.runBatch { writeBatch ->
                            for (selectedGroup in selectedArray) {
                                Log.e("mytag", selectedGroup.name)
                                writeBatch.update(
                                    db.collection("PlayGroup").document(selectedGroup.id),
                                    "user",
                                    FieldValue.arrayUnion(
                                        uid
                                    )
                                )
                            }

                        }.addOnSuccessListener {
                            Log.e("mytag", "updated ${selectedArray.size} Group")
                        }.addOnFailureListener { E ->
                            Log.e("mytag", "$E")
                        }
                        loadRecyclerView("PlayGroup")
                    }
                    alertDialog.show()

                }
            }.addOnFailureListener { e ->
                Log.e("mytag", "$e")
            }
    }

    fun addSubscription() {
        val alertDialog = AlertDialog.Builder(this)
        db.collection("Notification").whereEqualTo("admin", auth.currentUser!!.uid).get()
            .addOnSuccessListener { group ->
                if (group.documents.size > 0) {
                    var selectedArray = ArrayList<Object_Notification>()
                    var array = ArrayList<Object_Notification>()
                    for (groupObject in group.documents) {
                        if (uid !in groupObject.get("user") as ArrayList<String>) {
                            array.add(groupObject.toObject(Object_Notification::class.java)!!) //all group available
                        }
                    }
                    var layout = LinearLayout(this)
                    layout.orientation = LinearLayout.VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        600
                    )
                    lp.setMargins(50, 50, 70, 0)
                    var radioLayout =
                        LayoutInflater.from(this).inflate(R.layout.other_radiobuttons, layout, false)
                    var radioGroup = radioLayout.findViewById<RadioGroup>(R.id.radioGroup)
                    for (notiObject in array) {
                        val checkBox = CheckBox(this)
                        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                if (p1) {
                                    selectedArray.add(notiObject)
                                } else {
                                    selectedArray.remove(notiObject)
                                }
                            }
                        })
                        checkBox.id = array.indexOf(notiObject)
                        checkBox.text = notiObject.name
                        radioGroup.addView(checkBox)

                    }
                    layout.addView(radioLayout, lp)
                    alertDialog.setView(layout)
                    alertDialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Add") { dialog, which ->
                        db.runBatch { writeBatch ->
                            for (selectedGroup in selectedArray) {
                                Log.e("mytag", selectedGroup.name)
                                writeBatch.update(
                                    db.collection("Notification").document(selectedGroup.id),
                                    "user",
                                    FieldValue.arrayUnion(
                                        uid
                                    )
                                )
                            }

                        }.addOnSuccessListener {
                            Log.e("mytag", "updated ${selectedArray.size} Noti")
                        }.addOnFailureListener { E ->
                            Log.e("mytag", "$E")
                        }
                        loadRecyclerView("Notification")
                    }
                    alertDialog.show()
                }
            }.addOnFailureListener { e ->
                Log.e("mytag", "$e")
            }
    }
}


