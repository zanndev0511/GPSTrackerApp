package com.example.gpstrackerapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso

class MapCustomizationActivity : AppCompatActivity() {
    lateinit var map_type: ImageView
    lateinit var default_map_btn: Button
    lateinit var satelite_map_btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_customization)

        map_type = findViewById(R.id.map_type)
        default_map_btn = findViewById(R.id.btn_defaultMap)
        satelite_map_btn = findViewById(R.id.btn_sateliteMap)

        satelite_map_btn.setOnClickListener {
            default_map_btn.setBackgroundColor(Color.parseColor("#E91E63"))
            default_map_btn.setTextColor(Color.WHITE)

            satelite_map_btn.setBackgroundColor(Color.parseColor("#B8AEAE"))
            satelite_map_btn.setTextColor(Color.BLACK)
            Picasso.get().load(R.drawable.satelite_map).into(map_type)

            var map_cus = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("map_custom")
                .setValue("satelite")
            recreate()

//            var intent: Intent = Intent(this, UserLocationMainActivity::class.java)
//            intent.putExtra("map_type", "satelite")
//            startActivity(intent)
//            finish()
        }
        default_map_btn.setOnClickListener {
            satelite_map_btn.setBackgroundColor(Color.parseColor("#E91E63"))
            satelite_map_btn.setTextColor(Color.WHITE)

            default_map_btn.setBackgroundColor(Color.parseColor("#B8AEAE"))
            default_map_btn.setTextColor(Color.BLACK)

            Picasso.get().load(R.drawable.default_map).into(map_type)

            var map_cus = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("map_custom")
                .setValue("default")
            recreate()
//            var intent: Intent = Intent(this, UserLocationMainActivity::class.java)
//            intent.putExtra("map_type", "default")
//            startActivity(intent)
//            finish()
        }
        //            Check loại map để set nút
        var check_map = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("map_custom")
        check_map.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue().toString().equals("satelite")) {
                    default_map_btn.setBackgroundColor(Color.parseColor("#E91E63"))
                    default_map_btn.setTextColor(Color.WHITE)

                    satelite_map_btn.setBackgroundColor(Color.parseColor("#B8AEAE"))
                    satelite_map_btn.setTextColor(Color.BLACK)
                    Picasso.get().load(R.drawable.satelite_map).into(map_type)
                } else {
                    satelite_map_btn.setBackgroundColor(Color.parseColor("#E91E63"))
                    satelite_map_btn.setTextColor(Color.WHITE)

                    default_map_btn.setBackgroundColor(Color.parseColor("#B8AEAE"))
                    default_map_btn.setTextColor(Color.BLACK)

                    Picasso.get().load(R.drawable.default_map).into(map_type)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}