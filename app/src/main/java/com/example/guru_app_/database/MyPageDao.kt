package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper
import com.example.guru_app_.models.Book

class MyPageDao(context: Context) {
    private val dbHelper: SQLiteOpenHelper = BookDatabaseHelper(context)

    // Load monthly statistics (dummy implementation)
    fun loadMonthlyStatistics(userId: String): List<Statistic> {
        // Implement actual logic here to retrieve statistics from the database
        return listOf(
            Statistic("January", 5),
            Statistic("February", 8)
            // Add more dummy data or fetch from the database
        )
    }

    // Load user books
    fun loadUserBooks(userId: String): List<UserBook> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("user_books", null, "userId=?", arrayOf(userId), null, null, null)
        val userBooks = mutableListOf<UserBook>()
        with(cursor) {
            while (moveToNext()) {
                val userBook = UserBook(
                    getInt(getColumnIndexOrThrow("id")),
                    getString(getColumnIndexOrThrow("userId")),
                    getString(getColumnIndexOrThrow("isbn")),
                    getString(getColumnIndexOrThrow("status"))
                )
                userBooks.add(userBook)
            }
        }
        cursor.close()
        db.close()
        return userBooks
    }

    // 사용자 프로필 저장
    fun saveUserProfile(userId: String, name: String, phoneNumber: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("name", name)
            put("phone_number", phoneNumber)
        }
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // 사용자 프로필 로드
    fun loadUserProfile(userId: String): UserProfile? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("users", null, "user_id=?", arrayOf(userId), null, null, null)
        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"))
            cursor.close()
            UserProfile(userId, name, phoneNumber)
        } else {
            cursor.close()
            null
        }
    }


    // Load book by ISBN
    fun loadBook(isbn: String): Book? {
        val db = dbHelper.readableDatabase
        var book: Book? = null
        val cursor = db.query("books", null, "isbn=?", arrayOf(isbn), null, null, null)
        try {
            if (cursor.moveToFirst()) {
                book = Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("author")),
                    cursor.getString(cursor.getColumnIndexOrThrow("publisher")),
                    cursor.getString(cursor.getColumnIndexOrThrow("isbn")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cover_image")),
                    cursor.getString(cursor.getColumnIndexOrThrow("start_date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("end_date")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                    cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return book
    }

    data class UserProfile(val userId: String, val name: String, val phoneNumber: String)
    data class Statistic(val month: String, val booksRead: Int)
    data class UserBook(val id: Int, val userId: String, val isbn: String, val status: String)
}
