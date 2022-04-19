package com.example.testdb3.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class MyDBManager(context: Context) {
    val myDBHelper = MyDBHelper(context)
    var db: SQLiteDatabase ?= null

    fun openDb() {
        db = myDBHelper.writableDatabase
    }

    fun addChatToDB(title: String, uid: Int) {
        val values = ContentValues().apply {
            put(MyDbNameClass.CHATS_TITLE_COLUMN_NAME, title)
            put(MyDbNameClass.CHATS_UID_COLUMN_NAME, uid)
        }
        db?.insert(MyDbNameClass.CHATS_TABLE_NAME, null, values)
    }

    fun addFolderToDB(title: String, uid: Int) {
        val values = ContentValues().apply {
            put(MyDbNameClass.FOLDERS_TITLE_COLUMN_NAME, title)
            put(MyDbNameClass.FOLDERS_UID_COLUMN_NAME, uid)
        }
        db?.insert(MyDbNameClass.FOLDERS_TABLE_NAME, null, values)
    }

    fun addChatToFolder(chatID: Int, folderID: Int) {
        val values = ContentValues().apply {
            put(MyDbNameClass.FOLDERS_SHARING_FOLDER_ID_COLUMN_NAME, folderID)
            put(MyDbNameClass.FOLDERS_SHARING_CHAT_ID_COLUMN_NAME, chatID)
        }
        db?.insert(MyDbNameClass.FOLDERS_SHARING_TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun getChats(): ArrayList<String> {
        val dataList = ArrayList<String>()

        val cursor = db?.query(MyDbNameClass.CHATS_TABLE_NAME,
            null, null, null, null, null, null)

        while(cursor?.moveToNext()!!) {
                val dataText = cursor.getString(
                    cursor.getColumnIndex(MyDbNameClass.CHATS_TITLE_COLUMN_NAME))
                dataList.add(dataText.toString())
        }

        cursor.close()

        return dataList
    }

    @SuppressLint("Range")
    fun getFolders(): ArrayList<String> {
        val dataList = ArrayList<String>()

        val cursor = db?.query(MyDbNameClass.FOLDERS_TABLE_NAME,
            null, null, null, null, null, null)

        while(cursor?.moveToNext()!!) {
            val dataText = cursor.getString(
                cursor.getColumnIndex(MyDbNameClass.FOLDERS_TITLE_COLUMN_NAME))
            dataList.add(dataText.toString())
        }

        cursor.close()

        return dataList
    }

    @SuppressLint("Range")
    fun getChatByUID(uid: Int): String {
        var data = String()

        val cursor = db?.query(
            MyDbNameClass.CHATS_TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            "${MyDbNameClass.CHATS_UID_COLUMN_NAME} = ?",              // The columns for the WHERE clause
            arrayOf(uid.toString()),          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )

        while(cursor?.moveToNext()!!) {
            val dataText = cursor.getString(
                cursor.getColumnIndex(MyDbNameClass.CHATS_TITLE_COLUMN_NAME))
            data = dataText
        }

        cursor.close()

        return data
    }

    fun closeDB() {
        myDBHelper.close()
    }

}