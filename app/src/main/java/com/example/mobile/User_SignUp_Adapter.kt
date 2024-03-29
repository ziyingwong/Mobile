package com.example.mobile

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class User_SignUp_Adapter(options: FirestoreRecyclerOptions<Object_company>) :
    FirestoreRecyclerAdapter<Object_company, User_SignUp_Adapter.ViewHolder>(options) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): User_SignUp_Adapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_company) {
        holder.id.text = model.name
        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, User_SignUp_Details::class.java)
            intent.putExtra("link",model.registration)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            view.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}