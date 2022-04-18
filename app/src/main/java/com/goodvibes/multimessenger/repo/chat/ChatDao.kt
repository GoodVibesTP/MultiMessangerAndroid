package com.goodvibes.multimessenger.repo.chat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goodvibes.multimessenger.repo.chat.Chat

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addChat(chat: Chat)

    @Query("SELECT * FROM chats ORDER BY id DESC")
    fun getChats(): LiveData<List<Chat>>
}