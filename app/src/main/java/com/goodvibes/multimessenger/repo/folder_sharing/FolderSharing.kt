package com.goodvibes.multimessenger.repo.folder_sharing

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import com.goodvibes.multimessenger.repo.chat.Chat
import com.goodvibes.multimessenger.repo.folder.Folder

@Entity(tableName = "folder_sharing",
        primaryKeys = ["folderID", "chatID"],
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("folderID"),
            onDelete = CASCADE),
        ForeignKey(
            entity = Chat::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chatID"),
            onDelete = CASCADE)
    ]
)
data class FolderSharing (
    var folderID: Int,
    var chatID: Int,
)