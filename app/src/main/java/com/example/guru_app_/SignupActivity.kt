package com.example.guru_app_

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignupActivity : AppCompatActivity() {
    lateinit var back: TextView
    lateinit var signupbutton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // id가 signin인 버튼을 찾습니다.
        back = findViewById<TextView>(R.id.back)
        signupbutton = findViewById<Button>(R.id.signupbutton)

        // 버튼 클릭 리스너를 설정합니다.
        back.setOnClickListener {
            // 새로운 인텐트를 생성하여 SignupActivity로 이동합니다.
            //var할시 오류.. 초기화문제 한번만 사용되기 때문에. 다시 돌아오는경우엔?
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        signupbutton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}