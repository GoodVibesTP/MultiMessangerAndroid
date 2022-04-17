package com.goodvibes.multimessenger.usecase
import android.widget.Toast
import com.goodvibes.multimessenger.MainActivity
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Folder
import com.goodvibes.multimessenger.datastructure.idTGFolder
import com.goodvibes.multimessenger.datastructure.idVKFolder
import com.goodvibes.multimessenger.network.vkmessenger.VK

class MainActivityUC(_mainActivity: MainActivity, _vkMessenger: VK) {
    private val mainActivity = _mainActivity
    private val vk = _vkMessenger

    fun getAllChats(
        count: Int,
        first_msg: Int,
        callback: (MutableList<Chat>) -> Unit
    ) {
       vk.getAllChats(count, first_msg, callback = callback)
    }

    fun isLogin(): Boolean {
        return true;
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
        Toast.makeText(mainActivity, "Create folder " + chat.folder.name + " for chat " + chat.title, Toast.LENGTH_LONG).show()
    }

    fun moveChatToFolder(chat: Chat) {
        Toast.makeText(mainActivity, chat.title + " to folder " + chat.folder.name, Toast.LENGTH_LONG).show()
    }

    fun isChatInFolder(chat: Chat, folder: Folder) : Boolean {
        return true
    }
}