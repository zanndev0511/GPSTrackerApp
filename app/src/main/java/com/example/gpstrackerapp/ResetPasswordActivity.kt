package com.example.gpstrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var ok_btn: Button
    lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        progress = findViewById(R.id.progressBar)
        email = findViewById(R.id.email)
        ok_btn = findViewById(R.id.button_ok)

        ok_btn.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {task ->
                progress.visibility = View.VISIBLE
                if (task.isSuccessful){
                    progress.visibility = View.GONE
                    Toast.makeText(applicationContext, "Sent to your email!", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}