package com.example.guru_app_.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru_app_.BookDatabaseHelper
import com.example.guru_app_.models.Memo

class MemoDao(context: Context) {
    private val dbHelper: SQLiteOpenHelper = BookDatabaseHelper(context)

    fun addMemo(memo: Memo) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("book_id", memo.bookId)
            put("title", memo.title)
            put("content", memo.content)
            put("page", memo.page)
            put("image_path", memo.imagePath)
            put("created_at", memo.createdAt)
            put("updated_at", memo.updatedAt)
        }
        db.insert("memos", null, values)
        db.close()
    }

    fun updateMemo(memo: Memo) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("book_id", memo.bookId)
            put("title", memo.title)
            put("content", memo.content)
            put("page", memo.page)
            put("image_path", memo.imagePath)
            put("created_at", memo.createdAt)
            put("updated_at", memo.updatedAt)
        }
        db.update("memos", values, "id=?", arrayOf(memo.id.toString()))
        db.close()
    }

    fun getMemosForBook(bookId: Int): List<Memo> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "memos", null, "book_id=?", arrayOf(bookId.toString()),
            null, null, null
        )
        val memos = mutableListOf<Memo>()
        with(cursor) {
            while (moveToNext()) {
                val memo = Memo(
                    getInt(getColumnIndexOrThrow("id")),
                    getInt(getColumnIndexOrThrow("book_id")),
                    getString(getColumnIndexOrThrow("title")),
                    getString(getColumnIndexOrThrow("content")),
                    getInt(getColumnIndexOrThrow("page")),
                    getString(getColumnIndexOrThrow("image_path")),
                    getString(getColumnIndexOrThrow("created_at")),
                    getString(getColumnIndexOrThrow("updated_at"))
                )
                memos.add(memo)
            }
        }
        cursor.close()
        db.close()
        return memos
    }

    fun deleteMemo(memoId: Int) {
        val db = dbHelper.writableDatabase
        db.delete("memos", "id=?", arrayOf(memoId.toString()))
        db.close()
    }


    fun getMemoById(memoId: Int): Memo? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "memos", null, "id=?", arrayOf(memoId.toString()),
            null, null, null
        )
        var memo: Memo? = null
        with(cursor) {
            if (moveToFirst()) {
                memo = Memo(
                    getInt(getColumnIndexOrThrow("id")),
                    getInt(getColumnIndexOrThrow("book_id")),
                    getString(getColumnIndexOrThrow("title")),
                    getString(getColumnIndexOrThrow("content")),
                    getInt(getColumnIndexOrThrow("page")),
                    getString(getColumnIndexOrThrow("image_path")),
                    getString(getColumnIndexOrThrow("created_at")),
                    getString(getColumnIndexOrThrow("updated_at"))
                )
            }
        }
        cursor.close()
        db.close()
        return memo
    }
}
