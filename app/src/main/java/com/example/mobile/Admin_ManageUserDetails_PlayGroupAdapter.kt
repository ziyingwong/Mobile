package com.example.mobile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class Admin_ManageUserDetails_PlayGroupAdapter(options: FirestoreRecyclerOptions<Object_Playgroup>) :
    FirestoreRecyclerAdapter<Object_Playgroup, Admin_ManageUserDetails_PlayGroupAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent?.context).inflate(R.layout.admin_manage_userdetail_grouplist_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int, model: Object_Playgroup) {
        holder.name.text = model.name
        if (model.id in DataContainer_User.playGroupList) {
            holder.name.isChecked = true
        }
        holder.name.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true && model.id !in DataContainer_User.playGroupList) {
                DataContainer_User.playGroupList.add(model.id)
                Log.e("myTag",DataContainer_User.playGroupList.toString())
            } else if(isChecked ==false && model.id in DataContainer_User.playGroupList) {
                DataContainer_User.playGroupList.remove(model.id)
                Log.e("myTag",DataContainer_User.playGroupList.toString())

            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<CheckBox>(R.id.dataA)
    }
}