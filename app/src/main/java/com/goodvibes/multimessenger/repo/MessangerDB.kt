package com.goodvibes.multimessenger.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goodvibes.multimessenger.repo.chat.Chat
import com.goodvibes.multimessenger.repo.folder.Folder
import com.goodvibes.multimessenger.repo.folder_sharing.FolderSharing
import com.goodvibes.multimessenger.repo.chat.ChatDao
import com.goodvibes.multimessenger.repo.folder.FolderDao
import com.goodvibes.multimessenger.repo.folder_sharing.FolderSharingDao


@Database(entities = [Folder::class, Chat::class, FolderSharing::class], version = 1, exportSchema = false)
abstract class MessengerDB : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun chatDao(): ChatDao
    abstract fun folderSharingDao(): FolderSharingDao

    companion object {
        @Volatile
        private var INSTANCE: MessengerDB?= null

        fun getDatabase(context: Context): MessengerDB {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessengerDB::class.java,
                    "messenger_db",
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}

