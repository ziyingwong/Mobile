package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManageUserDetailsPage : AppCompatActivity() {

    //improvement : add search bar for group and playgroup

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    lateinit var adapterGroup: Admin_ManageUserDetails_GroupAdapter
    lateinit var adapterPlaygroup: Admin_ManageUserDetails_PlayGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_manage_user_details)
        var user = intent.getStringExtra("id")
        var uid = intent.getStringExtra("uid")
        val idtext = findViewById<TextView>(R.id.idText)
        idtext.text = user

        //show group list
        var queryGroup = db.collection("Group").whereEqualTo("admin", auth.currentUser!!.uid)
        var optionsGroup = FirestoreRecyclerOptions.Builder<Object_Group>()
            .setQuery(queryGroup, Object_Group::class.java)
            .build()
        var groupRecycler = findViewById<RecyclerView>(R.id.groupRecycler)
        adapterGroup = Admin_ManageUserDetails_GroupAdapter(optionsGroup)
        var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        groupRecycler.layoutManager = layoutManager
        groupRecycler.adapter = adapterGroup

        //show playgroup list
        var queryPlaygroup = db.collection("PlayGroup").whereEqualTo("admin", auth.currentUser!!.uid)
        var optionsPlaygroup = FirestoreRecyclerOptions.Builder<Object_Playgroup>()
            .setQuery(queryPlaygroup, Object_Playgroup::class.java)
            .build()
        var playgroupRecycler = findViewById<RecyclerView>(R.id.playGroupRecycler)
        adapterPlaygroup = Admin_ManageUserDetails_PlayGroupAdapter(optionsPlaygroup)
        var layoutManager2 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        playgroupRecycler.layoutManager = layoutManager2
        playgroupRecycler.adapter = adapterPlaygroup


        //update group
        val groupButton = findViewById<Button>(R.id.groupButton)
        groupButton.setOnClickListener {
            Log.e("myTag", "before new : ${DataContainer_User.groupList}")
            Log.e("myTag", "before old : ${DataContainer_User.OldGroupList}")
            var oldgrouplist = DataContainer_User.OldGroupList.clone() as ArrayList<String>
            var newgrouplist = DataContainer_User.groupList.clone() as ArrayList<String>
            for (i in newgrouplist.indices) {
                if (newgrouplist.get(i) !in oldgrouplist) {
                    db.collection("Group").document(newgrouplist.get(i))
                        .update("user", FieldValue.arrayUnion(uid)).addOnSuccessListener {
                            Log.e("myTag", "Added ${uid} to ${newgrouplist.get(i)}")
                        }
                }
            }
            for (i in oldgrouplist.indices) {
                if (oldgrouplist.get(i) !in newgrouplist) {
                    db.collection("Group").document(oldgrouplist.get(i))
                        .update("user", FieldValue.arrayRemove(uid))
                        .addOnSuccessListener {
                            Log.e("myTag", "Removed ${uid} from ${oldgrouplist.get(i)}")

                        }
                }
            }
            DataContainer_User.OldGroupList = DataContainer_User.groupList.clone() as ArrayList<String>
            Log.e("myTag", "after new : ${DataContainer_User.groupList}")
            Log.e("myTag", "after old : ${DataContainer_User.OldGroupList}")
            var intent = Intent(this, Admin_ManageUserDetailsPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.putExtra("id", user)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }

        //update playgroup
        val playgroupButton = findViewById<Button>(R.id.playGroupButon)
        playgroupButton.setOnClickListener {
            Log.e("myTag", "before new : ${DataContainer_User.playGroupList}")
            Log.e("myTag", "before old : ${DataContainer_User.OldPlayGroupList}")
            var oldplaygrouplist = DataContainer_User.OldPlayGroupList.clone() as ArrayList<String>
            var playgrouplist = DataContainer_User.playGroupList.clone() as ArrayList<String>
            for (i in playgrouplist.indices) {
                if (playgrouplist.get(i) !in oldplaygrouplist) {
                    db.collection("PlayGroup").document(playgrouplist.get(i))
                        .update("user", FieldValue.arrayUnion(uid)).addOnSuccessListener {
                            Log.e("myTag", "Added ${uid} to ${playgrouplist.get(i)}")
                        }
                }
            }
            for (i in oldplaygrouplist.indices) {
                if (oldplaygrouplist.get(i) !in playgrouplist) {
                    db.collection("PlayGroup").document(oldplaygrouplist.get(i))
                        .update("user", FieldValue.arrayRemove(uid))
                        .addOnSuccessListener {
                            Log.e("myTag", "Removed ${uid} from ${oldplaygrouplist.get(i)}")

                        }
                }
            }
            DataContainer_User.OldPlayGroupList = DataContainer_User.playGroupList.clone() as ArrayList<String>
            Log.e("myTag", "after new : ${DataContainer_User.playGroupList}")
            Log.e("myTag", "after old : ${DataContainer_User.OldPlayGroupList}")
            var intent = Intent(this, Admin_ManageUserDetailsPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.putExtra("id", user)
            intent.putExtra("uid", uid)
            startActivity(intent)
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

