package com.goodvibes.multimessenger.repo.folder_sharing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goodvibes.multimessenger.repo.folder_sharing.FolderSharing


@Dao
interface FolderSharingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFolderSharing(folderSharing: FolderSharing)

    @Query("SELECT * FROM folder_sharing ORDER BY folderID DESC, chatID DESC")
    fun getFolderSharing(): LiveData<List<FolderSharing>>
}