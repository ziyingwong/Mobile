package com.example.mobile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class Admin_ManagePlayGroupMainFragmentAdapter(options: FirestoreRecyclerOptions<Object_Playgroup>) :
    FirestoreRecyclerAdapter<Object_Playgroup, Admin_ManagePlayGroupMainFragmentAdapter.ViewHolder>(options) {

    var db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Admin_ManagePlayGroupMainFragmentAdapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_Playgroup) {
        holder.id.text = model.name
        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, Admin_ManagePlayGroupDetailsPage::class.java)
            intent.putExtra("id", model.id)
            intent.putExtra("name", model.name)
            view.context.startActivity(intent)
        }
    }

    fun deleteItem(position: Int, context: Context) {
        var alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Are you sure you want to delete Playgroup ${snapshots.getSnapshot(position).get("name")} ?")
        alertDialog.setNegativeButton("Cancel"){ dialog, which ->
            dialog.dismiss()
            this.notifyDataSetChanged()
        }
        alertDialog.setPositiveButton("Yes", object : DialogInterface.OnClickListener {

            override fun onClick(p0: DialogInterface?, p1: Int) {
                snapshots.getSnapshot(position).reference.delete()
            }
        })
        alertDialog.show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }
}