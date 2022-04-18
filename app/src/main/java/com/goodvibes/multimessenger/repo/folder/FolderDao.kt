package com.goodvibes.multimessenger.repo.folder

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goodvibes.multimessenger.repo.folder.Folder

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFolder(folder: Folder)

    @Query("SELECT * FROM folders ORDER BY id DESC")
    fun getFolders(): LiveData<List<Folder>>
}