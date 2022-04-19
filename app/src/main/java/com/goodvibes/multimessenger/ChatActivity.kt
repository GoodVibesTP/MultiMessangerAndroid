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
        toolbar.title = currentChat.title
        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        activityChatBinding.chatBtnSendMessage.setOnClickListener{
            val messageString = activityChatBinding.chatInputMessage.text.toString()
            if(!messageString.isEmpty()) {
                val message = Message(
                    text = messageString,
                    chatId = currentChat.chatId,
                    isMyMessage = true,
                    messenger = currentChat.messenger
                )
                usecase.sendMessage(message) { message_id ->
                    message.id = message_id
                    GlobalScope.launch(Dispatchers.Main) {
                        listMessage.add(message)
                        listMessageAdapter.notifyDataSetChanged()

                        activityChatBinding.chatInputMessage.text.clear()
                    }
                }
            }
        }

        initListMessage()
    }


    fun initListMessage() {
        listMessage = mutableListOf()
        listMessageAdapter = ListSingleChatAdapter(this@ChatActivity, listMessage);
        activityChatBinding.listMessage.setAdapter(listMessageAdapter)
        usecase.getMessageFromChat(currentChat, 50) { messages ->
            GlobalScope.launch(Dispatchers.Main) {
                listMessage.addAll(messages)
                listMessage.sortBy { message -> message.date }
                listMessageAdapter.notifyDataSetChanged()
            }
        }

    }
}