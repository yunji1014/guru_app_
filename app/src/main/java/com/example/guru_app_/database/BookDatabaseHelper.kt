package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "booklog.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USER_PHOTO = "user_photo"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_IMAGE = "image"
        //책 테이블 생성 쿼리
        private const val CREATE_BOOKS_TABLE = """
            CREATE TABLE books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                publisher TEXT,
                isbn TEXT NOT NULL UNIQUE,
                cover_image TEXT,
                start_date TEXT,
                end_date TEXT,
                rating REAL,
                status TEXT NOT NULL DEFAULT 'reading',
                genre TEXT
            );
        """
        // 메모 테이블 생성 쿼리
        private const val CREATE_MEMOS_TABLE = """
            CREATE TABLE memos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id INTEGER NOT NULL,
                title TEXT,
                content TEXT,
                page INTEGER,
                image_path TEXT,
                created_at TEXT,
                updated_at TEXT,
                FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
            );
        """
        // 통계 테이블 생성 쿼리
        private const val CREATE_STATISTICS_TABLE = """
            CREATE TABLE IF NOT EXISTS Statistics (
                user_id TEXT NOT NULL,
                month TEXT NOT NULL,
                books_read INTEGER DEFAULT 0,
                genre_stats TEXT,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                PRIMARY KEY (user_id, month)
            );
        """
        //사용자 테이블 생성 쿼리
        private const val CREATE_USERS_TABLE = """
            CREATE TABLE users (
                user_id TEXT PRIMARY KEY,
                name TEXT,
                phone_number TEXT
            );
        """
        //사용자-책 테이블 생성 쿼리
        private const val CREATE_USER_BOOKS_TABLE = """
            CREATE TABLE user_books (
                user_id TEXT,
                isbn TEXT,
                status TEXT,
                rating INTEGER,
                FOREIGN KEY (user_id) REFERENCES users (user_id),
                FOREIGN KEY (isbn) REFERENCES books (isbn),
                PRIMARY KEY (user_id, isbn)
            )
        """
        //사용자 프로필 사진 테이블 생성 쿼리
        private const val CREATE_TABLE_USER_PHOTO = """
            CREATE TABLE $TABLE_USER_PHOTO (
                $COLUMN_USER_ID TEXT PRIMARY KEY,
                $COLUMN_IMAGE BLOB
            )
        """






    }



    // onCreate 메서드는 데이터베이스가 처음 생성될 때 호출
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BOOKS_TABLE)
        db.execSQL(CREATE_MEMOS_TABLE)
        db.execSQL(CREATE_STATISTICS_TABLE)
        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_USER_BOOKS_TABLE)
        db.execSQL(CREATE_TABLE_USER_PHOTO)


    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS memos")
        db.execSQL("DROP TABLE IF EXISTS books")
        db.execSQL("DROP TABLE IF EXISTS Statistics")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS user_books")
        db.execSQL("DROP TABLE IF EXISTS user_photo")
        onCreate(db)

        android.util.Log.d("BookDatabaseHelper", "데이터베이스 선언 from $oldVersion to $newVersion")
    }

    fun saveUserProfileImage(userId: String, image: ByteArray) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_IMAGE, image)
        }
        db.insertWithOnConflict(TABLE_USER_PHOTO, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getUserProfileImage(userId: String): ByteArray? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USER_PHOTO, arrayOf(COLUMN_IMAGE), "$COLUMN_USER_ID = ?", arrayOf(userId), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getBlob(it.getColumnIndexOrThrow(COLUMN_IMAGE))
            }
        }
        return null
    }


}
