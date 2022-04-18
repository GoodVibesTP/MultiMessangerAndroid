package com.goodvibes.multimessenger.repo.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
)