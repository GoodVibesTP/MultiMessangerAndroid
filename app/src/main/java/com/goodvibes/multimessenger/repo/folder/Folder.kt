package com.goodvibes.multimessenger.repo.folder

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "folders")
data class Folder (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val name: String
)
