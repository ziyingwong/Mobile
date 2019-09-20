package com.example.mobile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class Admin_ManageUserMainFragmentAdapter(options: FirestoreRecyclerOptions<Object_User>) :
    FirestoreRecyclerAdapter<Object_User, Admin_ManageUserMainFragmentAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Admin_ManageUserMainFragmentAdapter.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card,p0,false)
            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int, model: Object_User) {
        holder.id.text =model.id
        holder.itemView.setOnClickListener{ view ->
            val intent = Intent(view.context,Admin_ManageUserDetailsPage::class.java)
            DataContainer_User.groupList = ArrayList(model.groupList)
            DataContainer_User.id = model.id
            DataContainer_User.playGroupList = ArrayList(model.playGroupList)
            DataContainer_User.uid = model.uid
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            view.context.startActivity(intent)

        }
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}