package com.goodvibes.multimessenger.repo.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.goodvibes.multimessenger.repo.folder.Folder
import com.goodvibes.multimessenger.repo.MessengerDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ChatRepo

    init {
        val chatDao = MessengerDB.getDatabase(application).chatDao()
        val folderSharingDao = MessengerDB.getDatabase(application).folderSharingDao()
        repository = ChatRepo(chatDao, folderSharingDao)
    }

    fun addChat(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addChat(chat)
        }
    }

    fun addChatToFolder(folder: Folder, chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addChatToFolder(folder, chat)
        }
    }

    fun getChats(): LiveData<List<Chat>> {
        return repository.getChats()
    }
}