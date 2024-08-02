package com.example.guru_app_

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_app_.shelf.BookShelfActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ARFilter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_filter)

        val toInstagramButton: Button = findViewById(R.id.to_instagram)
        toInstagramButton.setOnClickListener {
            val url = "https://www.instagram.com/ar/1996595037449441/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


    }
}