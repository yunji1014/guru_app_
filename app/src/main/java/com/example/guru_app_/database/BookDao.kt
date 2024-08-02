package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper
import com.example.guru_app_.models.Book
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookDao(context: Context) {
    private val dbHelper: SQLiteOpenHelper = BookDatabaseHelper(context)

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
        }
        db.insert("books", null, values)
        db.close()
    }

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

    fun deleteBook(bookId: Int?) {
        val db = dbHelper.writableDatabase
        db.delete("books", "id = ?", arrayOf(bookId.toString()))
        db.close()
    }

    companion object {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }
    }

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
