package com.goodvibes.multimessenger.usecase
import com.goodvibes.multimessenger.MainActivity
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.network.vkmessenger.VK

class MainActivityUC(_mainActivity: MainActivity, _vkMessenger: VK) {
    val mainActivity = _mainActivity
    val vk = _vkMessenger

    fun getAllChats(
        count: Int,
        first_msg: Int,
        callback: (List<Chat>) -> Unit
    ) {
       vk.getAllChats(count, callback = callback)
    }


}