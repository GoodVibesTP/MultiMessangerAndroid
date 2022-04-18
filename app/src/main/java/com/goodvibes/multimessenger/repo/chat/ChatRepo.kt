package com.goodvibes.multimessenger.repo.chat

import androidx.lifecycle.LiveData
import com.goodvibes.multimessenger.repo.folder.Folder
import com.goodvibes.multimessenger.repo.folder_sharing.FolderSharing
import com.example.dbtest.data.folder_sharing.FolderSharingDao

class ChatRepo(private val chatDao: ChatDao, private val folderSharingDao: FolderSharingDao) {
    fun getChats(): LiveData<List<Chat>> {
        return chatDao.getChats()
    }

    suspend fun addChat(chat: Chat) {
        chatDao.addChat(chat)
    }

    suspend fun addChatToFolder(folder: Folder, chat: Chat) {
        val folderSharing = FolderSharing(folder.id, chat.id)
        folderSharingDao.addFolderSharing(folderSharing)
    }
}

