package com.example.gpstrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import org.mindrot.jbcrypt.BCrypt

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var avt_image: ImageView
    lateinit var old_pass: TextView
    lateinit var new_pass: TextView
    lateinit var retype_pass: TextView
    lateinit var name: TextView
    lateinit var ok: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        avt_image = findViewById(R.id.avt_user)
        old_pass = findViewById(R.id.old_password)
        new_pass = findViewById(R.id.new_password)
        retype_pass = findViewById(R.id.editText_RetypePass)
        name = findViewById(R.id.name)
        ok = findViewById(R.id.btnChange)
//        Load ten cua nguoi dung
        var getName = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("name")
        getName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.getValue().toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
//Load hinh anh tu db
        var getAvt = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("imageUrl")
        getAvt.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.getValue().toString()).into(avt_image)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        ok.setOnClickListener {
            //        load mật khẩu cũ và so sánh với mật khẩu mới
            var getOldPass = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("password")
            getOldPass.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                var salt = BCrypt.gensalt()
                    if (BCrypt.checkpw(old_pass.text.toString(), snapshot.getValue().toString())) {
                        if (new_pass.text.toString() == retype_pass.text.toString()) {
                            var salt = BCrypt.gensalt()
                            getOldPass.setValue(BCrypt.hashpw(new_pass.text.toString(),salt))
                            FirebaseAuth.getInstance().currentUser!!.updatePassword(new_pass.text.toString())
                        }
                        else{
                            Toast.makeText(applicationContext, "Unmatched with new password!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(applicationContext,"Your current password is incorrect!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }
}