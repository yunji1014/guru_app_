package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.util.regex.Pattern


class SignupActivity : AppCompatActivity() {
    lateinit var dbHelper: DBHelper
    lateinit var back: TextView
    lateinit var idcheckbtn: AppCompatButton
    lateinit var signupbtn: AppCompatButton
    lateinit var id: EditText
    lateinit var password: EditText
    lateinit var repassword: EditText
    lateinit var name: EditText
    lateinit var birth1: EditText
    lateinit var birth2: EditText
    lateinit var birth3: EditText
    lateinit var mail: EditText
    var CheckId: Boolean = false

    public override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
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
        setContentView(R.layout.activity_sign_up)

        dbHelper = DBHelper(this)
        back = findViewById(R.id.back)
        idcheckbtn = findViewById(R.id.idcheckbutton)
        signupbtn = findViewById(R.id.signupbutton)

        id = findViewById(R.id.user_id)
        password = findViewById(R.id.password)
        repassword = findViewById(R.id.password2)
        name = findViewById(R.id.name)
        birth1 = findViewById(R.id.birth1)
        birth2 = findViewById(R.id.birth2)
        birth3 = findViewById(R.id.birth3)
        mail = findViewById(R.id.mail)

        //back버튼(로그인페이지로 이동)
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //아이디 중복확인 버튼
        idcheckbtn.setOnClickListener{
            val userid = id.text.toString()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{5,10}$"

            if(userid == ""){
                Toast.makeText(this@SignupActivity,
                    "아이디를 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                if(Pattern.matches(idPattern, userid)){
                    val checkid = dbHelper.checkID(userid)
                    if(checkid == false){
                        CheckId = true
                        Toast.makeText(this@SignupActivity,
                            "사용 가능한 아이디입니다.",
                            Toast.LENGTH_SHORT).show()
                        idcheckbtn.setEnabled(false)
                    }
                    else{
                        Toast.makeText(this@SignupActivity,
                            "이미 존재하는 아이디입니다.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@SignupActivity,
                        "아이디 형식이 옳지 않습니다.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        //회원가입 버튼
        signupbtn.setOnClickListener{
            val userid = id.text.toString()
            val pass = password.text.toString()
            val repass = repassword.text.toString()
            val name = name.text.toString()
            val birth1 = birth1.text.toString()
            val birth2 = birth2.text.toString()
            val birth3 = birth3.text.toString()
            val mail = mail.text.toString()
            val birth = birth1 + birth2 + birth3
            val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
            val mailpattern = android.util.Patterns.EMAIL_ADDRESS;

            if(userid == "" || pass == "" || repass == "" || name == "" ||
                birth1 == "" || birth2 == "" || birth3 == "" || mail == "")
                Toast.makeText(this@SignupActivity,
                    "회원정보를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT).show()
            else{
                //아이디 중복 확인 O
                if(CheckId == true) {
                    //비밀번호 형식 확인
                    if (Pattern.matches(pwPattern, pass)) {
                        // 비밀번호 재확인 O
                        if (pass == repass) {
                            //이메일 형식 확인
                            if(mailpattern.matcher(mail).matches()){
                                val insert = dbHelper!!.insertData(userid, pass, name, birth, mail)
                                //가입 성공시 메인화면으로 전환
                                if (insert == true) {
                                    Toast.makeText(
                                        this@SignupActivity,
                                        "회원가입을 축하합니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                //가입 실패
                                else {
                                    Toast.makeText(
                                        this@SignupActivity,
                                        "회원가입에 실패하였습니다. 다시 시도해주세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            //이메일 형식 맞지 않음
                            else {
                                Toast.makeText(
                                    this@SignupActivity,
                                    "이메일 형식이 올바르지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        //비밀번호 재확인 실패
                        else {
                            Toast.makeText(
                                this@SignupActivity,
                                "비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // 비밀번호 형식 맞지 않음
                    else{
                        Toast.makeText(
                            this@SignupActivity,
                            "비밀번호 형식이 옳지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                //아이디 중복확인 X
                else {
                    Toast.makeText(
                        this@SignupActivity,
                        "아이디 중복확인을 해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}