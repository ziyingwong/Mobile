package com.example.mobile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class Admin_ManageUserDetails_GroupAdapter(options: FirestoreRecyclerOptions<Object_Group>) :
    FirestoreRecyclerAdapter<Object_Group, Admin_ManageUserDetails_GroupAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(parent?.context).inflate(R.layout.admin_manage_userdetail_grouplist_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int, model: Object_Group) {
        holder.name.text = model.name
        if (model.id in DataContainer_User.groupList) {
            holder.name.isChecked = true
        }
        holder.name.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked==true){
                DataContainer_User.groupList.add(model.id)
            }else{
                DataContainer_User.groupList.remove(model.id)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<CheckBox>(R.id.dataA)
    }
}