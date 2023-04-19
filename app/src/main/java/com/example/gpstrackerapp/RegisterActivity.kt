package com.example.gpstrackerapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult

class RegisterActivity : AppCompatActivity() {
    lateinit var e3_email : EditText
    lateinit var auth: FirebaseAuth
    lateinit var dialog: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        dialog = findViewById(R.id.progressBar)

        e3_email = findViewById(R.id.editTextTextEmailAddress3)
        auth = FirebaseAuth.getInstance()
    }
    public fun goToPasswordActivity(v : View){

//        check if email is already register or not
        auth.fetchSignInMethodsForEmail(e3_email.text.toString())
            .addOnCompleteListener{task : Task<SignInMethodQueryResult> ->
                if (task.isSuccessful){
                    dialog.visibility = View.VISIBLE
                    var check: Boolean = !task.result?.signInMethods?.isEmpty()!!
                    if (!check){
//                        email does not exist, so we can create this email with user
                        var myIntent : Intent = Intent(this, PasswordActivity::class.java)
                        myIntent.putExtra("email",e3_email.text.toString())
                        startActivity(myIntent)
                        finish()
                    }
                    else{
                        dialog.visibility = View.GONE
                        Toast.makeText(applicationContext,"This email is already registered", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    dialog.visibility = View.GONE
                    Toast.makeText(applicationContext,"Wrong email or password", Toast.LENGTH_LONG).show()
                }
            }
    }
}