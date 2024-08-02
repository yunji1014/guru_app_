package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper

class MyPageDao(context: Context) {
    private val dbHelper: SQLiteOpenHelper = BookDatabaseHelper(context)

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

    // 월간 통계 저장
    fun saveMonthlyStatistics(userId: String, month: String, booksRead: Int, genreStats: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("month", month)
            put("books_read", booksRead)
            put("genre_stats", genreStats)
        }
        db.insertWithOnConflict("statistics", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // 월간 통계 로드
    fun loadMonthlyStatistics(userId: String): List<MonthlyStatistics> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("statistics", null, "user_id=?", arrayOf(userId), null, null, null)
        val statsList = mutableListOf<MonthlyStatistics>()
        while (cursor.moveToNext()) {
            val month = cursor.getString(cursor.getColumnIndexOrThrow("month"))
            val booksRead = cursor.getInt(cursor.getColumnIndexOrThrow("books_read"))
            val genreStats = cursor.getString(cursor.getColumnIndexOrThrow("genre_stats"))
            statsList.add(MonthlyStatistics(userId, month, booksRead, genreStats))
        }
        cursor.close()
        return statsList
    }

    // 도서 정보 저장
    fun saveBook(isbn: String, title: String, coverImage: String, category: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("isbn", isbn)
            put("title", title)
            put("cover_image", coverImage)
            put("category", category)
        }
        db.insertWithOnConflict("books", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // 사용자 도서 상태 저장
    fun saveUserBookStatus(userId: String, isbn: String, status: String, rating: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("isbn", isbn)
            put("status", status)
            put("rating", rating)
        }
        db.insertWithOnConflict("user_books", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // 사용자 도서 상태 로드
    fun loadUserBooks(userId: String): List<UserBook> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("user_books", null, "user_id=?", arrayOf(userId), null, null, null)
        val userBooksList = mutableListOf<UserBook>()
        while (cursor.moveToNext()) {
            val isbn = cursor.getString(cursor.getColumnIndexOrThrow("isbn"))
            val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            val rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"))
            userBooksList.add(UserBook(userId, isbn, status, rating))
        }
        cursor.close()
        return userBooksList
    }


    // 월간 통계 업데이트
    fun updateMonthlyStatistics(userId: String, month: String, booksRead: Int, genreStats: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("books_read", booksRead)
            put("genre_stats", genreStats)
        }
        db.update("statistics", values, "user_id=? AND month=?", arrayOf(userId, month))
    }
}

// 데이터 클래스 정의
data class UserProfile(val userId: String, val name: String, val phoneNumber: String)
data class MonthlyStatistics(val userId: String, val month: String, val booksRead: Int, val genreStats: String)
data class UserBook(val userId: String, val isbn: String, val status: String, val rating: Int)
