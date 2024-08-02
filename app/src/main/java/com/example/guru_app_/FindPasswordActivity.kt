package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FindPasswordActivity : AppCompatActivity() {
    lateinit var dbHelper: DBHelper
    lateinit var back: ImageButton
    lateinit var mail: EditText
    lateinit var new_password: EditText
    lateinit var new_password2: EditText
    lateinit var reset: Button

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
        setContentView(R.layout.activity_find_password)

        dbHelper = DBHelper(this)

        back = findViewById(R.id.btnbacklogin)
        mail = findViewById(R.id.mymail)
        new_password = findViewById(R.id.newpw)
        new_password2 = findViewById(R.id.newpw2)
        reset = findViewById(R.id.resetpw)

        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        reset.setOnClickListener{
            val email = mail.text.toString()
            val newPassword = new_password.text.toString()
            val confirmPassword = new_password2.text.toString()

            if (newPassword == confirmPassword) {
                val checkmail = dbHelper.checkMail(email)
                if(checkmail) {
                    val update = dbHelper.resetPassword(email, newPassword)
                    if(update){
                        Toast.makeText(
                            this@FindPasswordActivity,
                            "성공적으로 변경되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(
                            this@FindPasswordActivity,
                            "비밀번호 변겅에 실패했습니다. 다시 시도해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    Toast.makeText(this, "해당 이메일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
