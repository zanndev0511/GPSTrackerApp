package com.example.gpstrackerapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var e1: EditText
    lateinit var e2: EditText
    lateinit var forgot_pass: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        e1 = findViewById(R.id.editTextTextEmailAddress)
        e2 = findViewById(R.id.editTextTextPassword)
        forgot_pass = findViewById(R.id.forgot_pass)
        auth = FirebaseAuth.getInstance()

        //đổi màu chữ khi chạm vào
        forgot_pass.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    forgot_pass.setTextColor(Color.RED) // đổi màu chữ thành màu đỏ khi TextView được chạm vào
                    var intent_change = Intent(this, ResetPasswordActivity::class.java)
                    startActivity(intent_change)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    forgot_pass.setTextColor(Color.BLACK) // trở về màu chữ mặc định khi chạm vào kết thúc hoặc bị hủy
                }
            }
            true
        }
    }

    public fun login(v: View) {

        var salt = BCrypt.gensalt()
        val hashedPassword = "\$2a\$10\$ydxP9GFMl0h3PQb3Xw8vhuNwY2oRgpjJgsCpnRUGxEJ5QnmKaT4oW"

        auth.signInWithEmailAndPassword(e1.text.toString(), e2.text.toString())
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    var user: FirebaseUser? = auth.currentUser
                    if (user!!.isEmailVerified) {
                        var myIntent: Intent =
                            Intent(applicationContext, UserLocationMainActivity::class.java)
                        startActivity(myIntent)
                        Toast.makeText(
                            applicationContext,
                            "User logged in successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Email is not verified yet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}