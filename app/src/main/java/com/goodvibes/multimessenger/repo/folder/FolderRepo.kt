package com.goodvibes.multimessenger.repo.folder

import androidx.lifecycle.LiveData
import com.goodvibes.multimessenger.repo.folder.Folder

class FolderRepo(private val folderDao: FolderDao) {
    fun getFolders(): LiveData<List<Folder>> {
        return folderDao.getFolders()
    }

    suspend fun addFolder(folder: Folder) {
        folderDao.addFolder(folder)
    }
}
