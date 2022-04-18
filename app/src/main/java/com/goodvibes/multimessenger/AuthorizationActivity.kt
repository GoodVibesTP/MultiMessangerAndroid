package com.goodvibes.multimessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK
import com.goodvibes.multimessenger.usecase.AuthorizationActivityUC

class AuthorizationActivity : AppCompatActivity() {
    lateinit var useCase: AuthorizationActivityUC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        useCase = AuthorizationActivityUC(VK, Telegram)

        initBtn()
    }

    fun initBtn() {
        val buttonAuthVK = findViewById(R.id.button_auth_vk) as Button
        val buttonAuthTG = findViewById(R.id.button_auth_tg) as Button

        if (useCase.IsLoginVK()) {
            buttonAuthVK.text = "Logout VK"
            buttonAuthVK.setOnClickListener {
                Toast.makeText(this@AuthorizationActivity, "Logout vk clicked", Toast.LENGTH_SHORT).show()
            }
        } else {
            buttonAuthVK.text = "Auth VK"
            buttonAuthVK.setOnClickListener {
                Toast.makeText(this@AuthorizationActivity, "AUTH vk clicked", Toast.LENGTH_SHORT).show()
            }
        }

        if (useCase.isLoginTG()) {
            buttonAuthTG.text = "Logout TG"
            buttonAuthTG.setOnClickListener {
                Toast.makeText(this@AuthorizationActivity, "Logout TG clicked", Toast.LENGTH_SHORT).show()
                Telegram.logout()
            }
        } else {
            buttonAuthTG.text = "Auth TG"
            buttonAuthTG.setOnClickListener {
                Toast.makeText(this@AuthorizationActivity, "AUTH TG clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AuthorizationTGActivity::class.java)
                startActivity(intent)
            }
        }
    }

}