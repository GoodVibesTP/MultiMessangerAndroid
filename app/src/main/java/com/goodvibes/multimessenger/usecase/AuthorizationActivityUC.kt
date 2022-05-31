package com.goodvibes.multimessenger.usecase

import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK

class AuthorizationActivityUC(
    private val _vkMessenger: VK,
    private val _tgMessenger: Telegram
) {
    fun IsLoginVK(): Boolean {
        return _vkMessenger.isAuthorized()
    }

    fun isLoginTG(): Boolean {
        return _tgMessenger.isAuthorized()
    }
}
