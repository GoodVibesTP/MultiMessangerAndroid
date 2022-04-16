package com.goodvibes.multimessenger.usecase
import android.widget.Toast
import com.goodvibes.multimessenger.MainActivity
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Folder
import com.goodvibes.multimessenger.network.vkmessenger.VK

class MainActivityUC(_mainActivity: MainActivity, _vkMessenger: VK) {
    private val mainActivity = _mainActivity
    private val vk = _vkMessenger

    fun getAllChats(
        count: Int,
        first_msg: Int,
        callback: (MutableList<Chat>) -> Unit
    ) {
       vk.getAllChats(count, callback = callback)
    }

    fun isLogin(): Boolean {
        return true;
    }

    fun getAllFolders(): MutableList<Folder> {
        val resultFromBD = arrayOf<Folder>(Folder(1, 1," folder1"),
                                    Folder(2, 2," folder2"),
                                    Folder(3, 3," folder3"))

        //ну вот так, а что я могу поделать
        //TODO: надо подумать что будет если пользователь авторизован в вк или тг а в другом месте нет
        val result = mutableListOf<Folder>(Folder(-1,-1,"Create new folder"),
                                            Folder(-2,-2,"Telegram"),
                                            Folder(-3,-3,"VK"))
        result.addAll(resultFromBD)
        return result
    }

    fun deleteChat(chat: Chat) {
        Toast.makeText(mainActivity, "Chat for delete " + chat.title, Toast.LENGTH_SHORT).show()
    }
}