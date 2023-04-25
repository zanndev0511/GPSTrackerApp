package com.example.gpstrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.goodiebag.pinview.Pinview
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class JoinCircleActivity : AppCompatActivity() {
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var pinview: Pinview
    lateinit var reference: DatabaseReference
    lateinit var currentReference: DatabaseReference
    lateinit var user: FirebaseUser
    lateinit var auth: FirebaseAuth
    lateinit var join_user_id: String

    //    lateinit var join_user_id: String
    lateinit var circleReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_circle)
        pinview = findViewById(R.id.pinview)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        reference = FirebaseDatabase.getInstance().getReference().child("Users")
        currentReference =
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.uid)

        join_user_id = user.uid

    }

    public fun submitButtonClick(view: View) {
//        To check if the input code is present or not in database
//        if code is present, find that user, and create a node (Circle Member)
        var query: Query = reference.orderByChild("code").equalTo(pinview.value)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
//                    var createUser: CreateUser? = null
                    for (childDss: DataSnapshot in snapshot.children) {
                        var current_user_id =
                            FirebaseDatabase.getInstance().getReference().child(user.uid)
                        current_user_id.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot1: DataSnapshot) {
                                var current_user_id: String = snapshot1.key.toString()
                                circleReference =
                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(current_user_id).child("CircleMembers")
                                var join_user_id =
                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                        .orderByChild("code").equalTo(pinview.value)
                                join_user_id.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot2: DataSnapshot) {
                                        for (ds1 in snapshot2.children) {
                                            var join_user_id = ds1.key.toString()
//                                            Bạn bè kết nối vào vòng kết nối của mình
                                            var circlejoin = CircleJoin(ds1.key.toString())
//                                var join_user = FirebaseDatabase.getInstance().getReference().child("User")
//                                          Mình kết nối vào vòng tròn của bạn bè
                                            val circlejoin_friend = CircleJoin(current_user_id)

                                            circleReference.child(join_user_id)
                                                .setValue(circlejoin)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "User joined circle successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            var member_friend =
                                                FirebaseDatabase.getInstance().getReference()
                                                    .child("Users").child(join_user_id)
                                                    .child("CircleMembers").child(current_user_id)
                                                    .setValue(circlejoin_friend)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })

                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
//                        var code = childDss.child("code").getValue(Double::class.java)

//                        var createUser: CreateUser? = childDss.getValue(CreateUser::class.java)
//                        var createUser = childDss.child("code").getValue().toString()

//                        join_user_id = createUser!!.userid

//                        Log.i("B", createUser.toString())
//                        join_user_id = createUser!!.userid
//

                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Circle code is invalid!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}