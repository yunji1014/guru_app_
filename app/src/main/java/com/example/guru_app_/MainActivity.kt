package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Intent
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

    //화면 터치 이벤트 처리
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText
            && !view.javaClass.name.startsWith("android.webkit.")) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            // 키보드가 화면 밖을 터치하면 키보드를 숨김
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

        //로그인 버튼 클릭 이벤트 처리
        btnlogin.setOnClickListener{
            val mail = mail.text.toString()
            val pass = password.text.toString()

            //에디트 텍스트가 비어 있는 경우
            if(mail == "" || pass == ""){
                Toast.makeText(this@MainActivity,
                    "아이디와 비밀번호를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
            }
            else{ //모두 입력되어 있는 경우
                //회원정보 확인
                val check = dbHelper.checkMailpass(mail, pass)
                if (check == true){
                    Toast.makeText(this@MainActivity,
                        "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    // sharedPreferences에 로그인한 회원 이메일 저장
                    val preferences = getSharedPreferences("user_pref", MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putString("usermail", mail);
                    editor.apply();

                    // HomeActivity로 이동
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@MainActivity,
                        "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 비밀번호 찾기 클릭 이벤트 처리
        findpw.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        // 회원가입 클릭 이벤트 처리
        signup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}