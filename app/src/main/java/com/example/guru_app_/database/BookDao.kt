package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper
import com.example.guru_app_.models.Book
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookDao(context: Context)  {
    private val dbHelper: SQLiteOpenHelper = BookDatabaseHelper(context)
    // 월별 완독 책 수 가져오기
    fun getCompletedBooksCountByMonth(): Map<Int, Int> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT strftime('%m', end_date) AS month, COUNT(*) AS count
            FROM books
            WHERE status = 'completed' AND end_date IS NOT NULL
            GROUP BY month
        """
        val cursor = db.rawQuery(query, null)
        val completedBooksCountByMonth = mutableMapOf<Int, Int>()
        while (cursor.moveToNext()) {
            val month = cursor.getString(cursor.getColumnIndexOrThrow("month")).toInt()
            val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
            completedBooksCountByMonth[month] = count
        }
        cursor.close()
        db.close()
        return completedBooksCountByMonth
    }
    // 책 추가
    fun addBook(book: Book) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", book.title)
            put("author", book.author)
            put("publisher", book.publisher)
            put("isbn", book.isbn)
            put("cover_image", book.coverImage)
            put("start_date", getCurrentDate()) // 현재 날짜를 start_date에 저장
            put("end_date", book.endDate)
            put("rating", book.rating)
            put("status", book.status)
            //put("genre", book.genre)
        }
        db.insert("books", null, values)
        db.close()
    }
    //책 상태 업데이트 함수
    fun updateBookStatus(bookId: Int, status: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status", status)
            if (status == "completed") {
                put("end_date", getCurrentDate())
            }
        }
        try {
            db.update("books", values, "id=?", arrayOf(bookId.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
    //책 평점 업데이트 함수
    fun updateBookRating(bookId: Int, rating: Float) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("rating", rating)
        }
        try {
            db.update("books", values, "id=?", arrayOf(bookId.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
    // 책 아이디로 책 정보 가져오는 함수
    fun getBookById(bookId: Int): Book? {
        val db = dbHelper.readableDatabase
        var book: Book? = null
        val cursor = db.query(
            "books", null, "id=?", arrayOf(bookId.toString()),
            null, null, null
        )
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
                    cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    //cursor.getString(cursor.getColumnIndexOrThrow("genre"))

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
    //책 상태에 따라 목록 가져오기
    fun getBooksByStatus(status: String): List<Book> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("books", null, "status=?", arrayOf(status), null, null, null)
        val books = mutableListOf<Book>()
        with(cursor) {
            while (moveToNext()) {
                val book = Book(
                    getInt(getColumnIndexOrThrow("id")),
                    getString(getColumnIndexOrThrow("title")),
                    getString(getColumnIndexOrThrow("author")),
                    getString(getColumnIndexOrThrow("publisher")),
                    getString(getColumnIndexOrThrow("isbn")),
                    getString(getColumnIndexOrThrow("cover_image")),
                    getString(getColumnIndexOrThrow("start_date")),
                    getString(getColumnIndexOrThrow("end_date")),
                    getFloat(getColumnIndexOrThrow("rating")),
                    getString(getColumnIndexOrThrow("status")),
                    //getString(getColumnIndexOrThrow("genre"))
                )
                books.add(book)
            }
        }
        cursor.close()
        db.close()
        return books
    }


    //모든 책 가져오기
    fun getAllBooks(): List<Book> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("books", null, null, null, null, null, null)
        val books = mutableListOf<Book>()
        with(cursor) {
            while (moveToNext()) {
                val book = Book(
                    getInt(getColumnIndexOrThrow("id")),
                    getString(getColumnIndexOrThrow("title")),
                    getString(getColumnIndexOrThrow("author")),
                    getString(getColumnIndexOrThrow("publisher")),
                    getString(getColumnIndexOrThrow("isbn")),
                    getString(getColumnIndexOrThrow("cover_image")),
                    getString(getColumnIndexOrThrow("start_date")),
                    getString(getColumnIndexOrThrow("end_date")),
                    getFloat(getColumnIndexOrThrow("rating")),
                    getString(getColumnIndexOrThrow("status"))
                )
                books.add(book)
            }
        }
        cursor.close()
        db.close()
        return books
    }
    //책 삭제 함수
    fun deleteBook(bookId: Int?) {
        val db = dbHelper.writableDatabase
        db.delete("books", "id = ?", arrayOf(bookId.toString()))
        db.close()
    }

    companion object {//현재 날짜 가져오기
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }
    }
    //다음 책 아이디 가져오기
    private fun getNextId(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM books ORDER BY id", null)
        var nextId = 0

        if (cursor.moveToFirst()) {
            do {
                val currentId = cursor.getInt(0)
                if (currentId != nextId) {
                    break // 빈 ID를 찾음
                }
                nextId++
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return nextId
    }
}
