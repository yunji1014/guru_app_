package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var dbHelper: DBHelper
    lateinit var mail: EditText
    lateinit var password: EditText
    lateinit var btnlogin: Button
    lateinit var findpw: TextView
    lateinit var signup: TextView

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText
            && !view.javaClass.name.startsWith("android.webkit.")) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow((this.window.decorView.applicationWindowToken), 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        mail = findViewById(R.id.login_email)
        password = findViewById(R.id.login_password)
        findpw = findViewById(R.id.pw_find)
        signup = findViewById<TextView>(R.id.signup)
        btnlogin = findViewById<Button>(R.id.Login)

        //로그인 버튼 클릭
        btnlogin.setOnClickListener{
            val mail = mail.text.toString()
            val pass = password.text.toString()

            if(mail == "" || pass == ""){
                Toast.makeText(this@MainActivity,
                    "아이디와 비밀번호를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                val check = dbHelper.checkMailpass(mail, pass)
                if (check == true){
                    Toast.makeText(this@MainActivity,
                        "로그인 되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@MainActivity,
                        "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findpw.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        signup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}