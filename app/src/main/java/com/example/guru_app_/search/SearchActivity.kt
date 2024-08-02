package com.example.guru_app_.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_app_.HomeActivity
import com.example.guru_app_.R
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.models.Book
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var search: SearchView
    private val books = mutableListOf<Book>()
    private lateinit var bookDao: BookDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bookDao = BookDao(this)

        backButton = findViewById(R.id.BackButton)
        search = findViewById(R.id.search)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bookAdapter = BookAdapter(this, books, bookDao)
        recyclerView.adapter = bookAdapter

        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()) {
                    fetchBooks(query)
                } else {
                    Toast.makeText(this@SearchActivity, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val query = intent.getStringExtra("query")
        if (query != null) {
            search.setQuery(query, true)
        } else {
            Toast.makeText(this, "검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchBooks(query: String) {
        val thread = Thread {
            try {
                val result = getNaverBookSearch(query)
                runOnUiThread {
                    parseBookInfo(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "검색 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        thread.start()
    }

    private fun getNaverBookSearch(keyword: String): String {
        val clientID = "qIL4TXtqeLNoiACa14G5"
        val clientSecret = "hPPiIzBqS3"
        val sb = StringBuilder()

        try {
            val text = URLEncoder.encode(keyword, "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/search/book.json?query=$text"
            val url = URL(apiURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("X-Naver-Client-Id", clientID)
            connection.setRequestProperty("X-Naver-Client-Secret", clientSecret)
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
            } else {
                throw Exception("응답 코드: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun parseBookInfo(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")

            if (items != null) {
                books.clear()
                for (i in 0 until items.length()) {
                    val item = items.optJSONObject(i)
                    if (item != null) {
                        val title = item.optString("title", "제목 없음")
                        val author = item.optString("author", "저자 없음")
                        val image = item.optString("image", "")
                        val isbn = item.optString("isbn", "ISBN 없음")
                        val publisher = item.optString("publisher", "출판사 없음")

                        books.add(Book(
                            title = title,
                            author = author,
                            coverImage = image,
                            isbn = isbn,
                            publisher = publisher,
                            status = "reading"
                        ))
                    }
                }
                bookAdapter.notifyDataSetChanged()
            } else {
                Log.e("SearchActivity", "Items array is null")
                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "JSON 파싱 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
