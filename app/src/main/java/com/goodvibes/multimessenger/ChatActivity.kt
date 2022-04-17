package com.goodvibes.multimessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.goodvibes.multimessenger.datastructure.Chat

class ChatActivity : AppCompatActivity() {
    lateinit var currentChat: Chat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        currentChat = intent.extras!!.get("Chat") as Chat
        Toast.makeText(this, currentChat.title, Toast.LENGTH_LONG).show()
    }
}