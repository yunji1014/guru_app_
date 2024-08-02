package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "booklog.db"
        private const val DATABASE_VERSION = 1

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
                status TEXT NOT NULL DEFAULT 'reading'
            );
        """

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

        private const val CREATE_USERS_TABLE = """
            CREATE TABLE users (
                user_id TEXT PRIMARY KEY,
                name TEXT,
                phone_number TEXT
            );
        """

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



    }



    // onCreate 메서드는 데이터베이스가 처음 생성될 때 호출
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BOOKS_TABLE)
        db.execSQL(CREATE_MEMOS_TABLE)
        db.execSQL(CREATE_STATISTICS_TABLE)
        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_USER_BOOKS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS memos")
        db.execSQL("DROP TABLE IF EXISTS books")
        db.execSQL("DROP TABLE IF EXISTS Statistics")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS user_books")
        onCreate(db)
    }


}
