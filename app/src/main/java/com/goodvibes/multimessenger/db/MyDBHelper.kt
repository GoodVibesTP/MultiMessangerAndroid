package com.example.testdb3.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.testdb3.db.MyDbNameClass.CREATE_TABLE_CHATS
import com.example.testdb3.db.MyDbNameClass.CREATE_TABLE_FOLDERS
import com.example.testdb3.db.MyDbNameClass.CREATE_TABLE_FOLDERS_SHARING
import com.example.testdb3.db.MyDbNameClass.DATABASE_NAME
import com.example.testdb3.db.MyDbNameClass.DATABASE_VERSION
import com.example.testdb3.db.MyDbNameClass.DELETE_TABLE_CHATS
import com.example.testdb3.db.MyDbNameClass.DELETE_TABLE_FOLDERS
import com.example.testdb3.db.MyDbNameClass.DELETE_TABLE_FOLDERS_SHARING

class MyDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_CHATS)
        db?.execSQL(CREATE_TABLE_FOLDERS)
        db?.execSQL(CREATE_TABLE_FOLDERS_SHARING)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_TABLE_CHATS)
        db?.execSQL(DELETE_TABLE_FOLDERS)
        db?.execSQL(DELETE_TABLE_FOLDERS_SHARING)
        onCreate(db)
    }

}