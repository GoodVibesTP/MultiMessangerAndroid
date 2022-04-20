package com.goodvibes.multimessenger.db

import android.util.Log
import com.example.testdb3.db.MyDBManager
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Messengers

class MyDBUseCase(val dbManager: MyDBManager) {
    fun addPrimaryFolders() {
        dbManager.addFolderToDB("VK", -3)
        dbManager.addFolderToDB("TELEGRAM", -2)
    }

    fun addChatsToPrimaryFolderIfNotExist(chat: Chat) {
        dbManager.addChatToDB(chat.title, chat.chatId)
        var folderUID = 0
        if (chat.messenger == Messengers.VK) {
            folderUID = -3
        } else if (chat.messenger == Messengers.TELEGRAM) {
            folderUID = -2
        }

        dbManager.addChatToFolder(chat.chatId, folderUID)
        Log.d("low", "Successfully add new chat: ${chat.title}!")
    }
}