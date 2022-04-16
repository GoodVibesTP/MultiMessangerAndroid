package com.goodvibes.multimessenger.usecase

import android.widget.Toast
import com.goodvibes.multimessenger.AuthorizationTGActivity
import com.goodvibes.multimessenger.network.vkmessenger.VK

class AuthorizationTGActivityUC(_authorizationTGActivity: AuthorizationTGActivity, _vkMessenger: VK) {
    val authorizationTGActivity = _authorizationTGActivity
    val vk = _vkMessenger

    fun SendAuthCodeToPhone(number: String) {
        Toast.makeText(authorizationTGActivity, number, Toast.LENGTH_LONG).show()
    }

    fun CheckAuthCode (code: String) {
        Toast.makeText(authorizationTGActivity, code, Toast.LENGTH_LONG).show()
    }
}