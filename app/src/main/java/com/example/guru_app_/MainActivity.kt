package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var signin: TextView
    lateinit var loginbutton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // id가 signin인 버튼을 찾습니다.
        signin = findViewById<TextView>(R.id.signin)
        loginbutton = findViewById<Button>(R.id.loginbutton)

        // 버튼 클릭 리스너를 설정합니다.
        signin.setOnClickListener {
            // 새로운 인텐트를 생성하여 SignupActivity로 이동합니다.
            //var할시 오류.. 초기화문제
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        loginbutton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}