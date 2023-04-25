package com.example.gpstrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso


class MyCircleActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    var adapter: RecyclerView.Adapter<*>? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var createUser: CreateUser
    lateinit var namelist: ArrayList<CreateUser>
    lateinit var reference: DatabaseReference
    lateinit var usersReference: DatabaseReference
    lateinit var circlememberid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_circle)
        recyclerView = findViewById(R.id.recycleview)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        namelist = ArrayList()

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
//        Xóa bạn bè
        removeFriend()

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users")
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.uid)
            .child("CircleMembers")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                namelist.clear()
                if (snapshot.exists()) {
                    for (dss: DataSnapshot in snapshot.children) {
                        circlememberid = dss.child("circlememberid").getValue(String::class.java)!!
                        usersReference.child(circlememberid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    createUser =
                                        snapshot.getValue<CreateUser>(CreateUser::class.java)!!
                                    namelist.add(createUser)
                                    adapter!!.notifyDataSetChanged()
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        applicationContext,
                                        error.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        adapter = MembersAdapter(namelist, applicationContext)
        recyclerView.adapter = adapter
        (adapter as MembersAdapter).notifyDataSetChanged()
    }
    fun removeFriend() {
        var my_intent = intent
        var remove_friend_id = intent.getStringExtra("delete_friend")
//        Xóa bạn bè của mình
        var delete_Friend = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(auth.currentUser!!.uid).child("CircleMembers")
            .child(remove_friend_id.toString())
//              Xóa mình ở đối phương
        var delete_me = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(remove_friend_id.toString()).child("CircleMembers")
            .child(auth.currentUser!!.uid)

        if (remove_friend_id != null) {
            // Tạo đối tượng AlertDialog

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Unfriend")
            alertDialogBuilder.setMessage("Are you sure you want to unfriend?")
            // Nút xác nhận
            alertDialogBuilder.setPositiveButton("Confirm") { dialog, _ ->
                delete_Friend.removeValue()
                delete_me.removeValue()
                dialog.dismiss()
                Toast.makeText(applicationContext, "Unfriended successfully", Toast.LENGTH_SHORT)
                    .show()
                finish()
                recreate()
            }

            // Nút huỷ
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            alertDialogBuilder.create().show()
        }

    }
}