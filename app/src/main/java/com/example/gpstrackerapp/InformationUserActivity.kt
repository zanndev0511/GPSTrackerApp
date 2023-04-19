package com.example.gpstrackerapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.marginLeft
import androidx.core.view.setMargins
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class InformationUserActivity : AppCompatActivity() {
    lateinit var avt_user: ImageView
    lateinit var email: EditText
    lateinit var name: EditText
    lateinit var changePassword: TextView
    lateinit var edit1: TextView
    lateinit var edit2: TextView
    private var resultUri: Uri? = null
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_user)

        avt_user = findViewById(R.id.avt_user)
        email = findViewById(R.id.new_password)
        name = findViewById(R.id.old_password)
        changePassword = findViewById(R.id.changePassword)
        edit1 = findViewById(R.id.edit_icon1)
        edit2 = findViewById(R.id.edit_icon2)

        storageReference = FirebaseStorage.getInstance().getReference().child("Users_images")

        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // Use the returned uri.
                resultUri = result.uriContent
                val uriFilePath = result.getUriFilePath(this) // optional usage
                avt_user.setImageURI(resultUri)
                var sr: StorageReference = storageReference.child(FirebaseAuth.getInstance().currentUser!!.uid + ".jpg")
                Toast.makeText(applicationContext, "Change your avatar successfully!", Toast.LENGTH_SHORT).show()
                sr.putFile(resultUri!!)
            } else {
                // An error occurred.
                val exception = result.error
            }
        }
//        Người dùng click edit1 sửa tên
        edit1.setOnClickListener { task ->

            var alertDialog = AlertDialog.Builder(this)
                .setTitle("Edit name")
                .setMessage("Enter your new name")

            var input_name = EditText(this)
            var lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            input_name.layoutParams = lp
            alertDialog.setView(input_name)

            alertDialog.setPositiveButton("OK") { dialogInterface, i ->
                var update_name = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).child("name")
                    .setValue(input_name.text.toString())
                recreate()
            }
            alertDialog.setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.cancel()
            }
            alertDialog.create().show()
        }

        //        Người dùng click edit1 sửa Email
        edit2.setOnClickListener { task ->
            var alertDialog = AlertDialog.Builder(this)
                .setTitle("Edit Email")
                .setMessage("Enter your email")

            var input_email = EditText(this)
            var lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            input_email.layoutParams = lp
            alertDialog.setView(input_email)

            alertDialog.setPositiveButton("OK") { dialogInterface, i ->
                Toast.makeText(
                    applicationContext,
                    "Email sent for vertification",
                    Toast.LENGTH_LONG
                ).show()
                //        check if email is already register or not
                FirebaseAuth.getInstance()!!.fetchSignInMethodsForEmail(input_email.text.toString())
                    .addOnCompleteListener { task: Task<SignInMethodQueryResult> ->
                        if (task.isSuccessful) {
                            var check: Boolean = !task.result?.signInMethods?.isEmpty()!!
                            if (!check) {
                                var user = FirebaseAuth.getInstance().currentUser
                                // Get auth credentials from the user for re-authentication
                                var current_email =
                                    FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("email")
                                var current_pass =
                                    FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("password")

                                current_email.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        current_pass.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot1: DataSnapshot) {
                                                var credential = EmailAuthProvider
                                                    .getCredential(
                                                        snapshot.getValue().toString(),
                                                        snapshot1.getValue().toString()
                                                    ) // Current Login Credentials
                                                // Prompt the user to re-provide their sign-in credentials
                                                user?.reauthenticate(credential)
                                                    ?.addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
//                                                            Now change your email address
                                                            var user =
                                                                FirebaseAuth.getInstance().currentUser
                                                            user?.updateEmail(input_email.text.toString())
                                                                ?.addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        // Email đã được cập nhật thành công.
                                                                        user?.sendEmailVerification()
                                                                            ?.addOnCompleteListener { task ->
                                                                                if (task.isSuccessful) {
                                                                                    Toast.makeText(
                                                                                        applicationContext,
                                                                                        "Email sent for vertification",
                                                                                        Toast.LENGTH_LONG
                                                                                    ).show()
                                                                                } else {
                                                                                    Toast.makeText(
                                                                                        applicationContext,
                                                                                        "Could not send email!",
                                                                                        Toast.LENGTH_SHORT
                                                                                    ).show()
                                                                                }
                                                                            }
                                                                        if (user?.isEmailVerified == true) {
//                                                                            nếu xác minh rồi thì update ở cơ sở dữ liệu
                                                                            var update_name =
                                                                                FirebaseDatabase.getInstance()
                                                                                    .getReference()
                                                                                    .child("Users")
                                                                                    .child(
                                                                                        FirebaseAuth.getInstance().currentUser!!.uid
                                                                                    )
                                                                                    .child("email")
                                                                                    .setValue(
                                                                                        input_email.text.toString()
                                                                                    )
                                                                            recreate()
                                                                        } else {
                                                                            user?.updateEmail(
                                                                                snapshot.getValue()
                                                                                    .toString()
                                                                            )
                                                                        }


                                                                    } else {
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Change email fail!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                }
                                                        }
                                                    }
                                            }

                                            override fun onCancelled(error1: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "This email have already exist!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            }
            alertDialog.setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.cancel()
            }
            alertDialog.create().show()
        }

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
        var getEmail = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("email")
        getEmail.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                email.setText(snapshot.getValue().toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        var getAvt = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("imageUrl")
        getAvt.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.getValue().toString()).into(avt_user)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        avt_user.setOnClickListener {
            var options = arrayOf("See profile picture", "Choose another avatar")
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { dialog, which ->
                // Xử lý sự kiện khi người dùng chọn một tùy chọn
                when (which) {
                    0 -> {
                        var loadAvt = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("imageUrl")
                        loadAvt.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var intent_view =
                                    Intent(applicationContext, ViewImageActivity::class.java)
                                intent_view.putExtra("imageUrl", snapshot.getValue().toString())
                                startActivity(intent_view)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                    1 -> {

                        cropImage.launch(
                            options {
                                setGuidelines(CropImageView.Guidelines.ON)
                            }
                        )
                    }
                }

            }
            val dialog = builder.create()
            dialog.show()
//            Toast.makeText(applicationContext, " hihi", Toast.LENGTH_SHORT).show()
        }
//đổi màu chữ khi chạm vào
        changePassword.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    changePassword.setTextColor(Color.RED) // đổi màu chữ thành màu đỏ khi TextView được chạm vào
                    var intent_change = Intent(this, ChangePasswordActivity::class.java)
                    startActivity(intent_change)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    changePassword.setTextColor(Color.BLACK) // trở về màu chữ mặc định khi chạm vào kết thúc hoặc bị hủy
                }
            }
            true
        }
    }

    fun sendVerificationEmail() {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Email sent for vertification",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Could not send email!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}