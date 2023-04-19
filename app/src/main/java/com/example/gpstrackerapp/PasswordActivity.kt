package com.example.gpstrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import org.mindrot.jbcrypt.BCrypt

class PasswordActivity : AppCompatActivity() {
    lateinit var email: String
    lateinit var e2_password :EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        e2_password = findViewById(R.id.editTextTextPassword2)
        var myIntent : Intent = intent
        if (myIntent!=null){
            email = myIntent.getStringExtra("email").toString()
        }
    }
    public fun goToNamePicActivity(v :View){
        if (e2_password.text.toString().length >6){
            var myIntent: Intent = Intent(this, NameActivity::class.java)
            myIntent.putExtra("email", email)
            myIntent.putExtra("password",e2_password.text.toString())
            startActivity(myIntent)
            finish()
        }
        else{
            Toast.makeText(applicationContext,"Password length should be more than 6 characters", Toast.LENGTH_SHORT).show()
        }
    }
//    fun isValidPassword(password: String): Boolean {
//        val pattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}\$".toRegex()
//        return pattern.matches(password)
//    }
}