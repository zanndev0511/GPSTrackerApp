package com.example.gpstrackerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.mindrot.jbcrypt.BCrypt

class InviteCodeActivity : AppCompatActivity() {
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit var date: String
    lateinit var issharing: String
    lateinit var code: String
    var imageUri: Uri? = null
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null
    lateinit var reference: DatabaseReference
    lateinit var t : TextView
    lateinit var userId: String
    lateinit var proBar: ProgressBar
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_code)
        t = findViewById(R.id.textView)
        proBar = findViewById(R.id.progressBar2)
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference().child("Users_images")
        var myIntent: Intent = intent
        reference = FirebaseDatabase.getInstance().getReference().child("Users")
        if (myIntent != null) {
            name = myIntent.getStringExtra("name").toString()
            email = myIntent.getStringExtra("email").toString()
            password = myIntent.getStringExtra("password").toString()
            code = myIntent.getStringExtra("code").toString()
            issharing = myIntent.getStringExtra("isSharing").toString()
            imageUri = myIntent.getParcelableExtra<Uri>("imageUri")

        }
        t.setText(code)
    }
    @SuppressLint("SuspiciousIndentation")
    public fun registerUser(v :View){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    proBar.visibility = View.VISIBLE
//                  insert value
                    user = auth.currentUser
                    var salt = BCrypt.gensalt()
                    var createUser = CreateUser(name,email,BCrypt.hashpw(password,salt),"false",code,0.0,0.0,"na", user!!.uid, "null")
                    userId = user!!.uid

                    reference.child(userId).setValue(createUser)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
//                                save image to firebase storage
                            var sr: StorageReference = storageReference.child(user!!.uid + ".jpg")
                                sr.putFile(imageUri!!)
                                    .addOnCompleteListener { task->
                                        if (task.isSuccessful){
//                                            val storageRef = FirebaseStorage.getInstance().getReference() // Lấy tham chiếu đến Firebase Storage
//                                            val imageRef = storageRef.child(user!!.uid + ".jpg") // Lấy tham chiếu đến hình ảnh
                                            sr.downloadUrl.addOnSuccessListener { uri ->
                                                var download_image_path = uri.toString() // Lấy đường dẫn của hình ảnh
                                                reference.child(user!!.uid).child("imageUrl").setValue(download_image_path)
                                                    .addOnCompleteListener { task->
                                                        if (task.isSuccessful){
                                                            proBar.visibility = View.GONE
//                                                          Toast.makeText(applicationContext,"Email sent for verification. Check email!",Toast.LENGTH_SHORT).show()
                                                            sendVerificationEmail()
                                                            var myIntent: Intent = Intent(this,LoginActivity::class.java)
                                                            startActivity(myIntent)
                                                        }
                                                        else{
                                                            proBar.visibility = View.GONE
                                                            Toast.makeText(applicationContext,"User Registered fail!",Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                            }.addOnFailureListener { exception ->
                                                // Xử lý lỗi nếu có
                                            }
//                                            var download_image_path = task.getResult().metadata.toString()

                                        }
                                    }

                            }
                            else{
                                proBar.visibility = View.GONE
                                Toast.makeText(applicationContext,"User Registered fail!",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

    }
    public fun sendVerificationEmail(){
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext, "Email sent for vertification", Toast.LENGTH_SHORT).show()
                    finish()
                    auth.signOut()
                }
                else{
                    Toast.makeText(applicationContext, "Could not send email!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}