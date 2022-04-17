package com.goodvibes.multimessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.goodvibes.multimessenger.fragment.AuthorizationTGInputNumber
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK
import com.goodvibes.multimessenger.usecase.AuthorizationActivityUC
import com.goodvibes.multimessenger.usecase.AuthorizationTGActivityUC

class AuthorizationTGActivity : AppCompatActivity() {
    lateinit var useCase: AuthorizationTGActivityUC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization_tgactivity)

        val fragment1 = AuthorizationTGInputNumber()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container_auth_tg_activity, fragment1)
        transaction.commit()
        useCase = AuthorizationTGActivityUC(this, VK, Telegram)
    }

    fun SendCodeForTGAuth(number: String) {
        useCase.SendAuthCodeToPhone(number);
    }

    fun CheckAuthCode (code: String) {
        useCase.CheckAuthCode(code)
    }
}
