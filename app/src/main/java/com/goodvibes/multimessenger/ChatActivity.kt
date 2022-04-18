package com.goodvibes.multimessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.databinding.ActivityChatBinding
import com.goodvibes.multimessenger.usecase.ChatActivityUC
import com.goodvibes.multimessenger.util.ListSingleChatAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    lateinit var currentChat: Chat
    lateinit var listMessage: MutableList<Message>
    lateinit var listMessageAdapter: ListSingleChatAdapter
    lateinit var toolbar: Toolbar

    lateinit var activityChatBinding : ActivityChatBinding;
    lateinit var usecase : ChatActivityUC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = ActivityChatBinding.inflate(layoutInflater);
        setContentView(activityChatBinding.root)

        currentChat = intent.extras!!.get("Chat") as Chat
        usecase = ChatActivityUC(this)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        activityChatBinding.chatBtnSendMessage.setOnClickListener{
            val messageString = activityChatBinding.chatInputMessage.text.toString()
            if(!messageString.isEmpty()) {
                val message = Message(text=messageString, chatId = currentChat.chatId, messenger = currentChat.messenger)
                usecase.sendMessage(message) { }
            }
        }

        initListMessage()
    }


    fun initListMessage() {
        usecase.getMessageFromChat(currentChat, 100) { listMessage ->
            GlobalScope.launch(Dispatchers.Main) {
                listMessageAdapter = ListSingleChatAdapter(this@ChatActivity, listMessage);
                activityChatBinding.listMessage.setAdapter(listMessageAdapter);
            }
        }

    }
}