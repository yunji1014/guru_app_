package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper
import com.example.guru_app_.DBHelper
import com.example.guru_app_.models.Book

class MyPageDao(context: Context) {
    private val dbHelper: BookDatabaseHelper = BookDatabaseHelper(context) // BookDatabaseHelper 초기화
    private val dbHelper2: DBHelper = DBHelper(context) // DBHelper 초기화

    // 사용자 프로필 이미지 저장
    fun saveUserProfileImage(userId: String, image: ByteArray) {
        dbHelper.saveUserProfileImage(userId, image)
    }

    // 사용자 프로필 이미지 가져오기
    fun getUserProfileImage(userId: String): ByteArray? {
        return dbHelper.getUserProfileImage(userId)
    }

    // 월별 통계 데이터 불러오기
    fun loadMonthlyStatistics(userId: String): List<Statistic> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Statistics", null, "user_id=?", arrayOf(userId),
            null, null, null
        )
        val statistics = mutableListOf<Statistic>()
        with(cursor) {
            while (moveToNext()) {
                val statistic = Statistic(
                    getString(getColumnIndexOrThrow("month")),
                    getInt(getColumnIndexOrThrow("books_read"))
                )
                statistics.add(statistic)
            }
        }
        cursor.close()
        db.close()
        return statistics
    }

    // 사용자 책 데이터 불러오기
    fun loadUserBooks(userId: String): List<UserBook> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("user_books", null, "user_id=?", arrayOf(userId), null, null, null)
        val userBooks = mutableListOf<UserBook>()
        with(cursor) {
            while (moveToNext()) {
                val userBook = UserBook(
                    getInt(getColumnIndexOrThrow("id")),
                    getString(getColumnIndexOrThrow("user_id")),
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
    fun saveUserProfile(userId: String, name: String, birth: String, mail: String) {
        val db = dbHelper2.writableDatabase
        val stmt = db.compileStatement("UPDATE users SET id=?, name=?, birth=? WHERE mail=?")
        stmt.bindString(1, userId)
        stmt.bindString(2, name)
        stmt.bindString(3, birth)
        stmt.bindString(4, mail)
        stmt.executeUpdateDelete()
        db.close()
    }

    // 사용자 프로필 불러오기
    fun loadUserProfile(userMail: String): UserProfile? {
        val db = dbHelper2.readableDatabase
        val cursor = db.query("users", null, "mail=?", arrayOf(userMail), null, null, null)
        return if (cursor.moveToFirst()) {
            val userId = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val birth = cursor.getString(cursor.getColumnIndexOrThrow("birth"))
            cursor.close()
            UserProfile(userId, name, birth)
        } else {
            cursor.close()
            null
        }
    }

    // ISBN으로 책 데이터 불러오기
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

    // 사용자 프로필 데이터 클래스
    data class UserProfile(val userId: String, val name: String, val birth: String)

    // 통계 데이터 클래스
    data class Statistic(val month: String, val booksRead: Int)

    // 사용자 책 데이터 클래스
    data class UserBook(val id: Int, val userId: String, val isbn: String, val status: String)

    // 프로필 사진 데이터 클래스
    data class ProfilePhoto(var no: Long?, var image: ByteArray?)
}


