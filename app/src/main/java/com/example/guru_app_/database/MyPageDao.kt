package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper

class MyPageDao (context: Context) {
    private val dbHelper: SQLiteOpenHelper = BookDatabaseHelper(context)

    fun saveUserProfile(userId: String, name: String, phoneNumber: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("name", name)
            put("phone_number", phoneNumber)
        }
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun loadUserProfile(userId: String): UserProfile? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("users", arrayOf("user_id", "name", "phone_number"), "user_id=?", arrayOf(userId), null, null, null)
        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"))
            UserProfile(userId, name, phoneNumber)
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun saveStatistics(userId: String, month: String, booksRead: Int, genreStats: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("month", month)
            put("books_read", booksRead)
            put("genre_stats", genreStats)
        }
        db.insertWithOnConflict("statistics", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun loadMonthlyStatistics(userId: String): List<MonthlyStatistic> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("statistics", arrayOf("month", "books_read"), "user_id=?", arrayOf(userId), null, null, null)
        val statistics = mutableListOf<MonthlyStatistic>()
        while (cursor.moveToNext()) {
            val month = cursor.getString(cursor.getColumnIndexOrThrow("month"))
            val booksRead = cursor.getInt(cursor.getColumnIndexOrThrow("books_read"))
            statistics.add(MonthlyStatistic(month, booksRead))
        }
        cursor.close()
        return statistics
    }

    fun loadGenreStatistics(userId: String): Map<String, Float> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("statistics", arrayOf("genre_stats"), "user_id=?", arrayOf(userId), null, null, null)
        val genreStatsMap = mutableMapOf<String, Float>()
        if (cursor.moveToFirst()) {
            val genreStats = cursor.getString(cursor.getColumnIndexOrThrow("genre_stats"))
            // JSON 또는 CSV 형식을 파싱하여 Map으로 변환하는 로직 추가
            genreStats.split(",").forEach {
                val parts = it.split(":")
                if (parts.size == 2) {
                    genreStatsMap[parts[0]] = parts[1].toFloat()
                }
            }
        }
        cursor.close()
        return genreStatsMap
    }

    data class UserProfile(val userId: String, val name: String, val phoneNumber: String)
    data class MonthlyStatistic(val month: String, val booksRead: Int)
}