package com.example.guru_app_

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.app.AppCompatDelegate
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
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        loadStatistics()
        setupDarkModeSwitch()
        setupEditProfileButton()

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
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadStatistics() {
        // 막대 그래프 데이터 설정
        val barEntries = listOf(
            BarEntry(1f, 10f),
            BarEntry(2f, 15f),
            BarEntry(3f, 5f),
            BarEntry(4f, 8f),
            BarEntry(5f, 12f)
        )
        val barDataSet = BarDataSet(barEntries, "월간 통계")
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()

        // 파이 그래프 데이터 설정
        val pieEntries = listOf(
            PieEntry(40f, "소설"),
            PieEntry(30f, "비소설"),
            PieEntry(20f, "과학"),
            PieEntry(10f, "기타")
        )
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

            // 여기에 데이터베이스나 SharedPreferences를 사용하여 변경된 정보를 저장하는 코드를 추가합니다.
            sharedPreferences.edit().apply {
                putString("user_name", name)
                putString("user_id", id)
                putString("user_tel", tel)
                apply()
            }
        }
    }
}
