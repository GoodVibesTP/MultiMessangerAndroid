package com.goodvibes.multimessenger.usecase

import android.widget.Toast
import com.goodvibes.multimessenger.network.vkmessenger.VK

class AuthorizationActivityUC(_vkMessenger: VK) {
    val vk = _vkMessenger

    fun IsLoginVK(): Boolean {
        return true;
    }

    fun isLoginTG(): Boolean {
        return false;
    }
}