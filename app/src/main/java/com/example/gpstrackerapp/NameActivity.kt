package com.example.gpstrackerapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*


class NameActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var e_name: EditText
    private lateinit var circleImageView: CircleImageView
    private var resultUri: Uri? = null
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            resultUri = result.uriContent
            val uriFilePath = result.getUriFilePath(this) // optional usage
            circleImageView.setImageURI(resultUri)
        } else {
            // An error occurred.
            val exception = result.error
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)
        e_name = findViewById(R.id.editTextTextPersonName)
        circleImageView = findViewById(R.id.circleImageView)

        val myIntent: Intent = intent
        if (myIntent != null) {
            email = myIntent.getStringExtra("email").toString()
            password = myIntent.getStringExtra("password").toString()
        }
    }

    public fun selectImage(v: View) {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    public fun generateCode(v: View) {
        val myDate = Date()
        val format1 = SimpleDateFormat("yyyy-mm-dd hh:mm:ss a", Locale.getDefault())
        val date = format1.format(myDate)
        val r = Random()

        var n: Int = 100000 + r.nextInt(900000)
        val code = n.toString()

        resultUri?.let {
            var myIntent: Intent = Intent(this, InviteCodeActivity::class.java)
            myIntent.putExtra("name", e_name.text.toString())
            myIntent.putExtra("email", email)
            myIntent.putExtra("password", password)
            myIntent.putExtra("date", date)
            myIntent.putExtra("isSharing", "false")
            myIntent.putExtra("code", code)
            myIntent.putExtra("imageUri", resultUri)
            startActivity(myIntent)
            finish()
        } ?: run {
            Toast.makeText(applicationContext, "Please choose an image", Toast.LENGTH_SHORT).show()
        }
    }
}
