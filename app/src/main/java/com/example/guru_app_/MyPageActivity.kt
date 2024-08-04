package com.example.guru_app_

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.database.MyPageDao
import com.example.guru_app_.shelf.BookShelfActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class MyPageActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var switchDarkMode: Switch
    private lateinit var edtName: EditText
    private lateinit var edtID: EditText
    private lateinit var edtBirth: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imgProfile: ImageView

    private lateinit var myPageDao: MyPageDao
    private lateinit var bookDao: BookDao
    private lateinit var userId: String
    private lateinit var userMail: String

    private var isDarkModeChange = false

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val preferences = getSharedPreferences("user_pref", MODE_PRIVATE)
        val usermail = preferences.getString("usermail", "")

        // 하단 네비게이션바 아이템 선택 리스너 설정
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_bookshelf -> {
                    startActivity(Intent(this, BookShelfActivity::class.java))
                    true
                }
                R.id.navigation_arfilter -> {
                    startActivity(Intent(this, ARFilter::class.java))
                    true
                }
                R.id.navigation_mypage -> {
                    true
                }
                else -> false
            }
        }

        barChart = findViewById(R.id.barChart)
        // pieChart = findViewById(R.id.pieChart) // PieChart 초기화
        switchDarkMode = findViewById(R.id.switchDarkMode)
        edtName = findViewById(R.id.edtName)
        edtID = findViewById(R.id.edtID)
        edtBirth = findViewById(R.id.edtBirth)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        imgProfile = findViewById(R.id.imgProfile)
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        edtID.isEnabled = false  // ID 필드 비활성화

        myPageDao = MyPageDao(this)
        bookDao = BookDao(this)
        userId = "some_user_id" // 실제 사용자 ID로 변경 필요
        userMail = usermail + ""

        setupBarChart() // BarChart 데이터 로드
        // loadPieChart(bookDao) // PieChart 데이터 로드

        loadUserProfile()
        if (!isDarkModeChange) {
            loadStatistics()
        }
        setupDarkModeSwitch()
        setupEditProfileButton(userMail)
        setupProfileImage()
    }

    // BarChart 설정
    private fun setupBarChart() {
        val completedBooksCountByMonth = bookDao.getCompletedBooksCountByMonth()

        val entries = mutableListOf<BarEntry>()
        for (month in 1..12) {
            val count = completedBooksCountByMonth[month] ?: 0
            entries.add(BarEntry(month.toFloat(), count.toFloat()))
        }

        val dataSet = BarDataSet(entries, "완독 수")
        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setLabelCount(12, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            private val months = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
            override fun getFormattedValue(value: Float): String {
                return months.getOrElse(value.toInt() - 1) { "" }
            }
        }

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.granularity = 1f // 세로 축 간격을 1로 설정
        barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // 정수로 포맷팅
            }
        }

        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.invalidate() // 차트를 갱신합니다.
    }

    // 사용자 프로필 로드
    private fun loadUserProfile() {
        val userProfile = myPageDao.loadUserProfile(userMail)
        userProfile?.let {
            edtName.setText(it.name)
            edtID.setText(it.userId)
            edtBirth.setText(it.birth)
            edtBirth.isEnabled = false
            edtID.isEnabled = false
            edtName.isEnabled = false
            imgProfile.isEnabled = false
        }

        val profileImage = myPageDao.getUserProfileImage(userMail)
        profileImage?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            imgProfile.setImageBitmap(bitmap)
        }
    }

    // 통계 데이터 로드
    private fun loadStatistics() {
        val statistics = myPageDao.loadMonthlyStatistics(userId)
        val userBooks = myPageDao.loadUserBooks(userId)

        if (statistics.isEmpty() && userBooks.isEmpty()) {
            if (!isDarkModeChange) { // 다크모드 변경 시에는 Toast를 띄우지 않음
                Toast.makeText(this, "No statistics available", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val barEntries = ArrayList<BarEntry>()
        val genreCounts = mutableMapOf<String, Int>()

        statistics.forEachIndexed { index, stat ->
            barEntries.add(BarEntry(index.toFloat(), stat.booksRead.toFloat()))
        }

        userBooks.forEach { userBook ->
            val book = bookDao.getBookById(userBook.id)
            book?.let {
                genreCounts[it.publisher] = genreCounts.getOrDefault(it.publisher, 0) + 1
            }
        }

        val barDataSet = BarDataSet(barEntries, "Monthly Statistics")
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()

        val pieEntries = ArrayList<PieEntry>()
        genreCounts.forEach { (genre, count) ->
            pieEntries.add(PieEntry(count.toFloat(), genre))
        }

        val pieDataSet = PieDataSet(pieEntries, "Book Genres Statistics")
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }

    // 다크모드 스위치 설정
    private fun setupDarkModeSwitch() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDarkMode
        updateDarkMode(isDarkMode)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            isDarkModeChange = true
            updateDarkMode(isChecked)
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            isDarkModeChange = false
        }
    }

    // 다크모드 업데이트
    private fun updateDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // 프로필 수정 버튼 설정
    private fun setupEditProfileButton(usermail: String) {
        btnEditProfile.setOnClickListener {
            edtBirth.isEnabled = !edtBirth.isEnabled
            edtName.isEnabled = !edtName.isEnabled
            imgProfile.isEnabled = !imgProfile.isEnabled

            val name = edtName.text.toString()
            val id = edtID.text.toString()
            val birth = edtBirth.text.toString()

            myPageDao.saveUserProfile(id, name, birth, usermail)
        }
    }

    // 프로필 이미지 설정
    private fun setupProfileImage() {
        imgProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    // 이미지 선택 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imgProfile.setImageBitmap(bitmap)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()

                myPageDao.saveUserProfileImage(userMail, imageByteArray)
            }
        }
    }
}



