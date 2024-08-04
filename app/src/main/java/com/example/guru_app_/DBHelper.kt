package com.example.guru_app_

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (context: Context) : SQLiteOpenHelper(context, "LoginDB", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create Table users(id TEXT primary key, password TEXT NOT NULL, name TEXT NOT NULL, birth TEXT, mail TEXT NOT NULL, photo TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop Table if exists users")
    }

    // 새로운 회원 데이터 삽입
    fun insertData(id: String?, password: String?, name: String?, birth: String?, mail: String?): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("password", password)
        contentValues.put("name", name)
        contentValues.put("birth", birth)
        contentValues.put("mail", mail)
        val result = db.insert("users", null, contentValues)
        db.close()
        return if(result == -1L) false else true
    }

    //id 중복 확인
    fun checkID(id: String?): Boolean{
        val db = this.readableDatabase
        var res = true
        val cursor = db.rawQuery("Select * from users where id =?", arrayOf(id))
        if(cursor.count <= 0) res = false
        return res
    }

    //mail 중복 확인
    fun checkMail(mail: String?): Boolean{
        val db = this.readableDatabase
        var res = true
        val cursor = db.rawQuery("Select * from users where mail =?", arrayOf(mail))
        if(cursor.count <= 0) res = false
        return res
    }

    //이메일과 비밀번호 확인. 일치하는 정보 있으면 true 반환
    fun checkMailpass(mail: String?, password: String?) : Boolean{
        val db = this.readableDatabase
        var res = true
        val cursor = db.rawQuery("Select * from users where mail = ? and password = ?", arrayOf(mail, password))
        if(cursor.count <= 0) res = false
        return res
    }

    //비밀번호 재설정
    fun resetPassword(mail: String?, password: String?): Boolean {
        val db = this.writableDatabase
        val stmt = db.compileStatement("UPDATE users SET password=? WHERE mail=?")
        stmt.bindString(1, password)
        stmt.bindString(2, mail)

        return try {
            val rowsAffected = stmt.executeUpdateDelete()
            rowsAffected > 0 // 업데이트된 행이 있는 경우 true를 반환
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}