package com.example.guru_app_.shelf

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_app_.ARFilter
import com.example.guru_app_.HomeActivity
import com.example.guru_app_.MyPageActivity
import com.example.guru_app_.R
import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.example.guru_app_.database.BookDatabaseHelper
import com.google.android.material.tabs.TabLayout

class BookShelfActivity : AppCompatActivity() {
    private lateinit var readingFragment: ReadingFragment
    private lateinit var completeFragment: CompleteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_shelf)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        readingFragment = ReadingFragment()
        completeFragment = CompleteFragment()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> supportFragmentManager.beginTransaction().replace(R.id.main_view, readingFragment, "READING_FRAGMENT").commit()
                    1 -> supportFragmentManager.beginTransaction().replace(R.id.main_view, completeFragment).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 기본적으로 첫 번째 탭을 선택합니다.
        supportFragmentManager.beginTransaction().replace(R.id.main_view, readingFragment, "READING_FRAGMENT").commit()

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
}

