package com.goodvibes.multimessenger.usecase

import android.widget.Toast
import com.goodvibes.multimessenger.AuthorizationTGActivity
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK

class AuthorizationTGActivityUC(_authorizationTGActivity: AuthorizationTGActivity, _vkMessenger: VK, _tgMessenger: Telegram) {
    val authorizationTGActivity = _authorizationTGActivity
    val vk = _vkMessenger
    val tg = _tgMessenger

    fun SendAuthCodeToPhone(number: String) {
        Toast.makeText(authorizationTGActivity, number, Toast.LENGTH_LONG).show()
        tg.sendAuthPhone(number)
    }

    fun CheckAuthCode (code: String) {
        Toast.makeText(authorizationTGActivity, code, Toast.LENGTH_LONG).show()
        tg.sendAuthCode(code)
    }
}
