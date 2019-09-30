package com.example.mobile

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageUserDetailsPage:AppCompatActivity() {

  //improvement : add search bar for group and playgroup

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    lateinit var adapterGroup : Admin_ManageUserDetails_GroupAdapter
    lateinit var adapterPlaygroup : Admin_ManageUserDetails_PlayGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_manage_user_details)
        var user = intent.getStringExtra("id")
        var uid = intent.getStringExtra("uid")
        var grouplist = intent.getStringArrayListExtra("grouplist")
        var playgrouplist = intent.getStringArrayListExtra("playgrouplist")
        val idtext = findViewById<TextView>(R.id.idText)
        idtext.text = user

        //show group list
        var queryGroup = db.collection("Group").whereEqualTo("admin",auth.currentUser!!.uid)
        var optionsGroup = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(queryGroup,Object_Group::class.java)
            .build()
        var groupRecycler = findViewById<RecyclerView>(R.id.groupRecycler)
        adapterGroup = Admin_ManageUserDetails_GroupAdapter(optionsGroup)
        var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        groupRecycler.layoutManager = layoutManager
        groupRecycler.adapter = adapterGroup

        //show playgroup list
        var queryPlaygroup = db.collection("PlayGroup").whereEqualTo("admin",auth.currentUser!!.uid)
        var optionsPlaygroup = FirestoreRecyclerOptions.Builder<Object_Playgroup>()
            .setQuery(queryPlaygroup,Object_Playgroup::class.java)
            .build()
        var playgroupRecycler = findViewById<RecyclerView>(R.id.playGroupRecycler)
        adapterPlaygroup = Admin_ManageUserDetails_PlayGroupAdapter(optionsPlaygroup)
        var layoutManager2 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        playgroupRecycler.layoutManager = layoutManager2
        playgroupRecycler.adapter = adapterPlaygroup


        //update group
        val groupButton = findViewById<Button>(R.id.groupButton)
        groupButton.setOnClickListener {
            db.collection("user").document(uid).update("groupList",DataContainer_User.groupList)
                .addOnSuccessListener {
                    Toast.makeText(this,"Updated Group",Toast.LENGTH_LONG).show()
                    Log.e("myTag","Saved : Group ${DataContainer_User.groupList}")
                }
                .addOnFailureListener { e-> Log.e("myTag",e.toString()) }
        }

        //update playgroup - modify here
        val playgroupButton = findViewById<Button>(R.id.playGroupButon)
        playgroupButton.setOnClickListener {
            db.collection("user").document(uid).update("playGroupList",DataContainer_User.playGroupList)
                .addOnSuccessListener {
                    Toast.makeText(this,"Updated Playgroup",Toast.LENGTH_LONG).show()
                    Log.e("myTag","Saved : Playgroup ${DataContainer_User.playGroupList}")
                }
                .addOnFailureListener { e-> Log.e("myTag",e.toString()) }
        }

    }

    override fun onStart() {
        super.onStart()
        adapterGroup!!.startListening()
        adapterPlaygroup!!.startListening()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        DataContainer_User.groupList.clear()
        DataContainer_User.playGroupList.clear()
    }

    override fun onStop() {
        super.onStop()
        if (adapterGroup != null) {
            adapterGroup.stopListening()
        }
        if (adapterPlaygroup != null) {
            adapterPlaygroup.stopListening()
        }
    }


}

