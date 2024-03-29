package com.example.mobile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class User_GroupDetails_Adapter(options: FirestoreRecyclerOptions<Object_Scene>) :
    FirestoreRecyclerAdapter<Object_Scene, User_GroupDetails_Adapter.ViewHolder>(options) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): User_GroupDetails_Adapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_Scene) {
        holder.id.text = model.name
        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, General_ViewScene::class.java)
            intent.putExtra("id",model.id)
            view.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}