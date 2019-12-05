package com.example.mobile

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


//playgroup
class Admin_ManageUserPlayGroupAdapter(options: FirestoreRecyclerOptions<Object_Playgroup>) :
    FirestoreRecyclerAdapter<Object_Playgroup, Admin_ManageUserPlayGroupAdapter.ViewHolder>(options) {
    var db = FirebaseFirestore.getInstance()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int, p2: Object_Playgroup) {

        p0.id.text = p2.name

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_list_fragment_card, parent, false)
        return Admin_ManageUserPlayGroupAdapter.ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }

    fun deleteItem(position: Int, uid: String) {
        var playgroup = snapshots.get(position).id
        db.collection("PlayGroup").document(playgroup).update("user", FieldValue.arrayRemove(uid))
            .addOnFailureListener { e ->
                Log.e("mytag", e.toString())
            }.addOnSuccessListener {
                this.notifyDataSetChanged()
                Log.e("mytag", "deleted")
            }
    }
}

//group
class Admin_ManageUserGroupAdapter(options: FirestoreRecyclerOptions<Object_Group>) :
    FirestoreRecyclerAdapter<Object_Group, Admin_ManageUserGroupAdapter.ViewHolder>(options) {
    var db = FirebaseFirestore.getInstance()
    override fun onBindViewHolder(p0: ViewHolder, p1: Int, p2: Object_Group) {
        p0.id.text = p2.name

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_list_fragment_card, parent, false)
        return Admin_ManageUserGroupAdapter.ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }

    fun deleteItem(position: Int, uid: String) {
        var group = snapshots.get(position).id
        db.collection("Group").document(group).update("user", FieldValue.arrayRemove(uid)).addOnFailureListener { e ->
            Log.e("mytag", e.toString())
        }.addOnSuccessListener {4
            Log.e("mytag", "deleted")
        }
    }
}

class Admin_ManageUserNotificationAdapter(options: FirestoreRecyclerOptions<Object_Notification>) :
    FirestoreRecyclerAdapter<Object_Notification, Admin_ManageUserNotificationAdapter.ViewHolder>(options) {
    var db = FirebaseFirestore.getInstance()
    override fun onBindViewHolder(p0: ViewHolder, p1: Int, p2: Object_Notification) {
        p0.id.text = p2.name

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_list_fragment_card, parent, false)
        return Admin_ManageUserNotificationAdapter.ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }

    fun deleteItem(position: Int, uid: String) {
        var subscription = snapshots.get(position).id
        db.collection("Notification").document(subscription).update("user", FieldValue.arrayRemove(uid))
            .addOnFailureListener { e ->
                Log.e("mytag", e.toString())
            }.addOnSuccessListener {
                Log.e("mytag", "deleted")
                this.notifyDataSetChanged()
            }
    }
}