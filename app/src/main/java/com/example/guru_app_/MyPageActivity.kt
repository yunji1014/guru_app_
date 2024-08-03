package com.example.guru_app_


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.guru_app_.R
import com.example.guru_app_.database.MyPageDao
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.shelf.BookShelfActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var switchDarkMode: Switch
    private lateinit var edtName: EditText
    private lateinit var edtID: EditText
    private lateinit var edtTel: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imgProfile: ImageView

    private lateinit var myPageDao: MyPageDao
    private lateinit var bookDao: BookDao
    private lateinit var userId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
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
        pieChart = findViewById(R.id.pieChart)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        edtName = findViewById(R.id.edtName)
        edtID = findViewById(R.id.edtID)
        edtTel = findViewById(R.id.edtTel)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        imgProfile = findViewById(R.id.imgProfile)
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        edtID.isEnabled = false  // Disable the ID field

        myPageDao = MyPageDao(this)
        bookDao = BookDao(this)
        userId = "some_user_id" // 실제 사용자 ID로 변경 필요

        loadUserProfile()
        loadStatistics()
        setupDarkModeSwitch()
        setupEditProfileButton()
    }

    private fun loadUserProfile() {
        val userProfile = myPageDao.loadUserProfile(userId)
        userProfile?.let {
            edtName.setText(it.name)
            edtID.setText(it.userId)
            edtTel.setText(it.phoneNumber)
            edtTel.isEnabled = false
            edtID.isEnabled = false
            edtName.isEnabled = false
            Glide.with(this).load(it.profileUrl).into(imgProfile)
        }
    }

    private fun loadStatistics() {
        val statistics = myPageDao.loadMonthlyStatistics(userId)
        val userBooks = myPageDao.loadUserBooks(userId)

        if (statistics.isEmpty() && userBooks.isEmpty()) {
            Toast.makeText(this, "No statistics available", Toast.LENGTH_SHORT).show()
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

    private fun setupDarkModeSwitch() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDarkMode
        updateDarkMode(isDarkMode)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            updateDarkMode(isChecked)
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
        }
    }

    private fun updateDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setupEditProfileButton() {
        btnEditProfile.setOnClickListener {
            edtTel.isEnabled = !edtTel.isEnabled
            edtName.isEnabled = !edtName.isEnabled
            val name = edtName.text.toString()
            val id = edtID.text.toString()
            val tel = edtTel.text.toString()

            myPageDao.saveUserProfile(userId, name, tel)

            val profileImageUri: Uri? = null // Get the URI of the new profile image if available
            if (profileImageUri != null) {
                // Save the profile image locally or to a server if needed and update the user profile with the new image URL/path
                // Example:
                // val profileImagePath = saveProfileImage(profileImageUri)
                // myPageDao.updateUserProfileImage(userId, profileImagePath)
            }
        }
    }

    // Add a function to save the profile image if necessary
    // private fun saveProfileImage(uri: Uri): String {
    //     // Implement logic to save the image locally or to a server and return the path/URL
    // }
}
