package com.example.mobile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class User_PlayGroupMainFragmentAdapter(options: FirestoreRecyclerOptions<Object_Playgroup>) :
    FirestoreRecyclerAdapter<Object_Playgroup, User_PlayGroupMainFragmentAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): User_PlayGroupMainFragmentAdapter.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card,p0,false)
            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int, model: Object_Playgroup) {
        holder.id.text =model.name
        holder.itemView.setOnClickListener{ view ->
            val intent = Intent(view.context,User_PlayGroupDetailsPage::class.java)
            intent.putExtra("id",model.id)
            intent.putExtra("name",model.name)
            view.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}