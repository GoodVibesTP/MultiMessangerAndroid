package com.goodvibes.multimessenger.repo.folder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.goodvibes.multimessenger.repo.MessengerDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel(application: Application): AndroidViewModel(application) {
    private val repository: FolderRepo

    init {
        val folderDao = MessengerDB.getDatabase(application).folderDao()
        repository = FolderRepo(folderDao)
    }

    fun addFolder(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFolder(folder)
        }
    }

    fun getFolders(): LiveData<List<Folder>> {
        return repository.getFolders()
    }
}