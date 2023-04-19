package com.example.gpstrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewImageActivity : AppCompatActivity() {
    lateinit var view_image :ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        view_image = findViewById(R.id.view_image)

        var intent_view = intent
        var image_url = intent_view.getStringExtra("imageUrl")
        Picasso.get().load(image_url).into(view_image)
    }
}