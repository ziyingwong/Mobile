package com.example.mobile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManagePlayGroupDetails_Adapter(
    options: FirestoreRecyclerOptions<Object_Scene>,
    id: String
) :
    FirestoreRecyclerAdapter<Object_Scene, Admin_ManagePlayGroupDetails_Adapter.ViewHolder>(options) {
    var id = id
    var db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): Admin_ManagePlayGroupDetails_Adapter.ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_Scene) {
        holder.id.text = model.name
        holder.itemView.setOnClickListener { view ->
            var scenes = ArrayList<String>()
            db.collection("scene").whereArrayContains("playgroup", id).get()
                .addOnSuccessListener { doc ->
                    for (i in doc.documents.indices) {
                        var scene = doc.documents.get(i).get("id")
                        scenes.add(scene.toString())

                    }
                    val intent = Intent(view.context, General_PlayScenes::class.java)
                    intent.putExtra("selected", position)
                    intent.putExtra("list", scenes)
                    Log.e("myTag", "${id} : ${scenes}")
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    view.context.startActivity(intent)

                }

        }
    }

    fun deleteItem(context: Context, position: Int) {
        var alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Are you sure you want to delete scene ${snapshots.getSnapshot(position).get("name")} ?")
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
            this.notifyDataSetChanged()
        }
        alertDialog.setPositiveButton("Yes", object : DialogInterface.OnClickListener {

            override fun onClick(p0: DialogInterface?, p1: Int) {
                //modified


                db.collection("scene").document(snapshots.getSnapshot(position).id)
                    .update("playgroup", FieldValue.arrayRemove(id))
                    .addOnSuccessListener {
                        if (snapshots.size < 1) {
                            db.collection("PlayGroup").document(id).update("imageUrl", "")

                        } else {
                            db.collection("PlayGroup").document(id)
                                .update("imageUrl", snapshots.getSnapshot(0).get("thumbnail"))
                        }
                        Log.e("mytag", "removed")

                    }
                    .addOnFailureListener { E ->
                        Log.e("mytag", "$E")
                    }
            }
        })
        alertDialog.show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}