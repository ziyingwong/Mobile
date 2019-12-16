package com.example.mobile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.internal.SharedResourceHolder
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import com.google.firebase.firestore.SetOptions
import java.util.zip.Inflater


class Admin_ManageGroupDetails_Adapter(options: FirestoreRecyclerOptions<Object_Scene>, id: String) :
    FirestoreRecyclerAdapter<Object_Scene, Admin_ManageGroupDetails_Adapter.ViewHolder>(options) {
    var id = id
    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Admin_ManageGroupDetails_Adapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.admin_list_fragment_card, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Object_Scene) {
        holder.id.text = model.name
        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, General_ViewScene::class.java)
            intent.putExtra("id", model.id)
            Log.e("myTag", model.name + ":" + model.id)
            view.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = itemView.findViewById<TextView>(R.id.dataA)
    }

    fun changeGroup(context: Context, model: Object_Scene) {

        //id = old group id
        //model = scene removed
        //

        db.collection("Group").whereEqualTo("admin", auth.currentUser!!.uid)
            .get().addOnSuccessListener { groupSnapshot ->
                val groupList = groupSnapshot.documents
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Change group")
                var layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    600
                )
                lp.setMargins(50, 50, 70, 0)
                var radioLayout = LayoutInflater.from(context).inflate(R.layout.other_radiobuttons, layout, false)
                var radioGroup = radioLayout.findViewById<RadioGroup>(R.id.radioGroup)
                for (group in groupList) {
                    val radioButton = RadioButton(context)
                    if (model.group.equals(group.id)) {
                        radioButton.isChecked = true
                    }
                    radioButton.id = groupList.indexOf(group)
                    radioButton.text = group.get("name").toString()
                    radioGroup.addView(radioButton)

                }
                layout.addView(radioLayout, lp)
                alertDialog.setView(layout)
                alertDialog.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                    this.notifyDataSetChanged()
                }
                alertDialog.setPositiveButton("Save") { dialog, which ->
                    var newGroup = radioGroup.checkedRadioButtonId
                    for (group in groupList) {
                        if (newGroup == groupList.indexOf(group)) {
                            Log.e("mytag", group.id)
                            var data = hashMapOf(
                                "group" to group.id
                            )
                            db.collection("scene").document(model.id).set(data, SetOptions.merge())
                                .addOnSuccessListener {
                                    db.runBatch { writeBatch ->
                                        if (snapshots.size < 1) {
                                            writeBatch.update(db.collection("Group").document(id), "imageUrl", "")
                                        } else {
                                            writeBatch.update(
                                                db.collection("Group").document(id),
                                                "imageUrl",
                                                snapshots.getSnapshot(0).get("thumbnail")
                                            )
                                        }
                                        writeBatch.update(
                                            db.collection("Group").document(group.id),
                                            "imageUrl",
                                            model.thumbnail
                                        )
                                    }.addOnFailureListener { e ->
                                        Log.e("mytag", "$e")
                                    }.addOnSuccessListener {
                                        Log.e("mytag", "success")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("mytag", e.toString())
                                }


                            break
                        }
                    }
                    this.notifyDataSetChanged()
                }
                alertDialog.show()
            }
    }
}