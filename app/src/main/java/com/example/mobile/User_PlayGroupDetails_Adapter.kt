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
import com.google.firebase.firestore.FirebaseFirestore

class User_PlayGroupDetails_Adapter(
    options: FirestoreRecyclerOptions<Object_Scene>,
    id: String
) :
    FirestoreRecyclerAdapter<Object_Scene, User_PlayGroupDetails_Adapter.ViewHolder>(options) {
    var id = id
    var db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): User_PlayGroupDetails_Adapter.ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_Scene) {
        holder.id.text = model.name
        holder.itemView.setOnClickListener { view ->

            db.collection("PlayGroup").document(id).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val intent = Intent(view.context, General_PlayScenes::class.java)
                    var scenes = doc.get("scene") as ArrayList<String>
                    intent.putExtra("selected", position)
                    intent.putExtra("list", scenes)
                    Log.e("myTag","${id} : ${scenes}")
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    view.context.startActivity(intent)

                }
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}