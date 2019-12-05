package com.example.mobile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class Admin_ManageUserMainFragmentAdapter(options: FirestoreRecyclerOptions<Object_User>) :
    FirestoreRecyclerAdapter<Object_User, Admin_ManageUserMainFragmentAdapter.ViewHolder>(options) {
    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    var grouplist = ArrayList<String>()
    var playgrouplist = ArrayList<String>()

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): Admin_ManageUserMainFragmentAdapter.ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_User) {
        holder.id.text = model.id
        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, Admin_ManageUserDetail::class.java)
            intent.putExtra("id", model.id)
            intent.putExtra("uid", model.uid)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            view.context.startActivity(intent)

//            db.collection("Group").whereEqualTo("admin", auth.currentUser!!.uid)
//                .whereArrayContains("user", model.uid).get()
//                .addOnSuccessListener { doc ->
//                    for (i in doc.documents.indices) {
//                        var group = doc.documents.get(i).get("id").toString()
//                        grouplist.add(group)
//                    }
//                    DataContainer_User.OldGroupList = grouplist.clone() as ArrayList<String>
//                    DataContainer_User.groupList = grouplist
//                    db.collection("PlayGroup").whereEqualTo("admin", auth.currentUser!!.uid)
//                        .whereArrayContains("user", model.uid).get()
//                        .addOnSuccessListener { doc ->
//                            for (i in doc.documents.indices) {
//                                var group = doc.documents.get(i).get("id").toString()
//                                playgrouplist.add(group)
//                            }
//                            DataContainer_User.playGroupList = playgrouplist
//                            DataContainer_User.OldPlayGroupList = playgrouplist.clone() as ArrayList<String>
//                            val intent = Intent(view.context, Admin_ManageUserDetailsPage::class.java)
//                            intent.putExtra("id", model.id)
//                            intent.putExtra("uid", model.uid)
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                            view.context.startActivity(intent)
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("myTag", e.toString())
//                        }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("myTag", e.toString())
//                }
        }
    }

//    fun deleteItem(position: Int, context: Context, model: Object_User) {
//        val alertDialog = AlertDialog.Builder(context)
//        alertDialog.setTitle("Delete")
//        alertDialog.setMessage("Are you sure you want to delete User ${snapshots.getSnapshot(position).get("name")} ?")
//        alertDialog.setNegativeButton("Cancel") { dialog, which ->
//            dialog.dismiss()
//            this.notifyDataSetChanged()
//        }
//        alertDialog.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
//            override fun onClick(p0: DialogInterface?, p1: Int) {
////                snapshots.getSnapshot(position).reference.delete()
//                //do server to delete
//            }
//        })
//        alertDialog.show()
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }


}