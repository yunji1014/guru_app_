package com.example.guru_app_

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_app_.shelf.BookShelfActivity
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.popapi.BestsellerResponse
import com.example.guru_app_.popapi.PopBookAdapter
import com.example.guru_app_.popapi.RetrofitClient
import com.example.guru_app_.search.SearchActivity
import com.example.guru_app_.shelf.BookImageAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var bookImageAdapter: BookImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var popBook: RecyclerView
    private lateinit var bookDao: BookDao
    private lateinit var bestsellerAdapter: PopBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bookDao = BookDao(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val books = bookDao.getAllBooks()
        bookImageAdapter = BookImageAdapter(this, books, bookDao)
        recyclerView.adapter = bookImageAdapter

        popBook = findViewById(R.id.PopBook)
        popBook.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fetchBestsellers()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // do nothing to prevent reloading HomeActivity
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

        // 기본 화면 설정
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_home
        }

        val search = findViewById<SearchView>(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val intent = Intent(this@HomeActivity, SearchActivity::class.java)
                    intent.putExtra("query", query)
                    startActivity(intent)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun fetchBestsellers() {
        val apiKey = "ttb1014jiye1521001"
        val queryType = "Bestseller"
        val maxResults = 5
        val start = 1
        val searchTarget = "Book"

        RetrofitClient.instance.getBestsellers(apiKey, queryType, maxResults, start, searchTarget)
            .enqueue(object : Callback<BestsellerResponse> {
                override fun onResponse(call: Call<BestsellerResponse>, response: Response<BestsellerResponse>) {
                    if (response.isSuccessful) {

                        val books = response.body()?.items ?: mutableListOf()
                        bestsellerAdapter = PopBookAdapter(books)
                        popBook.adapter = bestsellerAdapter
                    } else {
                        val errorBody = response.errorBody()?.string()
                    }
                }

                override fun onFailure(call: Call<BestsellerResponse>, t: Throwable) {
                }
            })
    }
}
