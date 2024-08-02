package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.guru_app_.database.MyPageDao
import com.example.guru_app_.database.UserProfile
import com.example.guru_app_.database.MonthlyStatistics
import com.example.guru_app_.database.UserBook
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.bumptech.glide.Glide

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

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

        loadUserProfile()
        loadStatistics()
        setupDarkModeSwitch()
        setupEditProfileButton()
    }

    private fun loadUserProfile() {
        val userId = "some_user_id" // Replace with the actual user ID
        val userProfile = myPageDao.loadUserProfile(userId)
        userProfile?.let {
            edtName.setText(it.name)
            edtID.setText(it.userId)
            edtTel.setText(it.phoneNumber)
            // Load the profile image using Glide if the path or URL is available in userProfile
            // For example, if profileUrl is stored in userProfile:
            // Glide.with(this).load(userProfile.profileUrl).into(imgProfile)
        }
    }

    private fun loadStatistics() {
        val userId = "some_user_id" // Replace with the actual user ID
        val statistics = myPageDao.loadMonthlyStatistics(userId)
        val userBooks = myPageDao.loadUserBooks(userId)

        val barEntries = ArrayList<BarEntry>()
        val genreCounts = mutableMapOf<String, Int>()

        statistics.forEachIndexed { index, stat ->
            Log.d("MyPageActivity", "Month: ${stat.month}, BooksRead: ${stat.booksRead}")
            barEntries.add(BarEntry(index.toFloat(), stat.booksRead.toFloat()))
        }

        userBooks.forEach { userBook ->
            val book = myPageDao.loadBook(userBook.isbn)
            book?.let {
                Log.d("MyPageActivity", "Book Category: ${it.category}")
                genreCounts[it.category] = genreCounts.getOrDefault(it.category, 0) + 1
            }
        }

        val barDataSet = BarDataSet(barEntries, "월간 통계")
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()

        val pieEntries = ArrayList<PieEntry>()
        genreCounts.forEach { (genre, count) ->
            pieEntries.add(PieEntry(count.toFloat(), genre))
        }

        val pieDataSet = PieDataSet(pieEntries, "도서 분야 통계")
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
            val name = edtName.text.toString()
            val id = edtID.text.toString()
            val tel = edtTel.text.toString()

            val userId = "some_user_id" // Replace with the actual user ID

            // Update user profile in SQLite database
            myPageDao.saveUserProfile(userId, name, tel)

            // Handle profile image upload if changed
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




