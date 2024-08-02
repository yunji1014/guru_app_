package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

        loadUserProfile()
        loadStatistics()
        setupDarkModeSwitch()
        setupEditProfileButton()
    }

    private fun loadUserProfile() {
        val userId = "some_user_id" // Replace with the actual user ID
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    edtName.setText(document.getString("name"))
                    edtID.setText(document.getString("id"))
                    edtTel.setText(document.getString("tel"))
                    val profileUrl = document.getString("profileUrl")
                    if (profileUrl != null && profileUrl.isNotEmpty()) {
                        Glide.with(this).load(profileUrl).into(imgProfile)
                    }
                }
            }
    }

    private fun loadStatistics() {
        // Replace with your actual user ID
        val userId = "some_user_id"

        db.collection("users").document(userId).collection("statistics").get()
            .addOnSuccessListener { result ->
                val barEntries = ArrayList<BarEntry>()
                val pieEntries = ArrayList<PieEntry>()

                for (document in result) {
                    val type = document.getString("type")
                    if (type == "monthly") {
                        barEntries.add(BarEntry(document.getDouble("month")!!.toFloat(), document.getDouble("value")!!.toFloat()))
                    } else if (type == "genre") {
                        pieEntries.add(PieEntry(document.getDouble("percentage")!!.toFloat(), document.getString("label")!!))
                    }
                }

                val barDataSet = BarDataSet(barEntries, "월간 통계")
                val barData = BarData(barDataSet)
                barChart.data = barData
                barChart.invalidate()

                val pieDataSet = PieDataSet(pieEntries, "도서 분야 통계")
                val pieData = PieData(pieDataSet)
                pieChart.data = pieData
                pieChart.invalidate()
            }
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
            val userId = "some_user_id" // Replace with the actual user ID

            val profileUpdates = hashMapOf(
                "name" to name,
                "tel" to tel
            )

            db.collection("users").document(userId).update(profileUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    // Profile updated successfully
                }
                .addOnFailureListener {
                    // Handle failure
                }

            // Handle profile image upload if changed
            val profileImageUri: Uri? = null // Get the URI of the new profile image if available
            if (profileImageUri != null) {
                val storageRef = storage.reference.child("profile_images/$userId.jpg")
                storageRef.putFile(profileImageUri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            db.collection("users").document(userId).update("profileUrl", uri.toString())
                        }
                    }
            }
        }
    }
}


