package com.goodvibes.multimessenger.usecase
import android.widget.Toast
import com.example.testdb3.db.DBConst
import com.example.testdb3.db.DBConst.RANDOM_END
import com.example.testdb3.db.DBConst.RANDOM_START
import com.goodvibes.multimessenger.MainActivity
import com.goodvibes.multimessenger.datastructure.*
import com.goodvibes.multimessenger.db.MyDBUseCase
import com.goodvibes.multimessenger.network.Messenger
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK
import java.util.*

class MainActivityUC(_mainActivity: MainActivity, _vkMessenger: VK, _tgMessenger: Telegram, _dbUseCase: MyDBUseCase) {
    private val mainActivity = _mainActivity
    private val vk = _vkMessenger
    private val tg = _tgMessenger
    private val dbUseCase = _dbUseCase

    fun getAllChats(
        count: Int,
        first_msg: Int,
        callback: (MutableList<Chat>) -> Unit
    ) {
        if (vk.isAuthorized()) {
            vk.getAllChats(count, first_msg, callback = callback)
        }
        if (tg.isAuthorized()) {
            tg.getAllChats(count, first_msg, callback = callback)
        }
    }

    fun getAllChats(
        count: Int,
        first_msg: Int,
        messenger: Messenger,
        callback: (MutableList<Chat>) -> Unit
    ) {
        messenger.getAllChats(count, first_msg, callback = callback)
    }

    fun isLogin(): Boolean {
        if (vk.isAuthorized()) {
            return true
        }
        else if (tg.isAuthorized()){
            return true
        }
        return false
    }

    fun isLogin(messenger: Messenger): Boolean {
        return messenger.isAuthorized()
    }

    fun getAllFolders(): MutableList<Folder> {
        val resultFromBD = arrayOf<Folder>(Folder(1, " folder1"),
                                    Folder(2, " folder2"),
                                    Folder(3, " folder3"))

        //ну вот так, а что я могу поделать
        //TODO: надо подумать что будет если пользователь авторизован в вк или тг а в другом месте нет
        val result = mutableListOf<Folder>(
                                            Folder(idTGFolder,"Telegram"),
                                            Folder(idVKFolder,"VK"))
        result.addAll(resultFromBD)
        return result
    }

    fun deleteChat(chat: Chat) {
        Toast.makeText(mainActivity, "Chat for delete " + chat.title, Toast.LENGTH_SHORT).show()
    }

    fun addFolder(chat: Chat) {
        val uniqueID = (RANDOM_START..RANDOM_END).random()
        dbUseCase.dbManager.addFolderToDB( chat.folder.name ,uniqueID)
        dbUseCase.dbManager.addChatToFolder(chat.chatId, uniqueID)
        Toast.makeText(mainActivity, "Create folder " + chat.folder.name + " for chat " + chat.title, Toast.LENGTH_LONG).show()
    }

    fun moveChatToFolder(chat: Chat) {
        val folderUID = dbUseCase.dbManager.getFolderByName(chat.folder.name)
        dbUseCase.dbManager.addChatToFolder(chat.chatId, folderUID)
        Toast.makeText(mainActivity, chat.title + " to folder " + chat.folder.name, Toast.LENGTH_LONG).show()
    }

    fun isChatInFolder(chat: Chat, folder: Folder) : Boolean {
        return true
    }

    fun startUpdateListener(callback: (Event) -> Unit) {
        vk.startUpdateListener(callback)
        tg.startUpdateListener(callback)
    }

    fun getChatByID(messenger: Messengers?, chatId: Long, callback: (Chat) -> Unit) {
        when (messenger) {
            Messengers.VK -> {
                vk.getChatById(chatId, callback)
            }
            Messengers.TELEGRAM -> {
                tg.getChatById(chatId, callback)
            }
        }
    }
}