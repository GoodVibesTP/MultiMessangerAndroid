package com.goodvibes.multimessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.databinding.ActivityChatBinding
import com.goodvibes.multimessenger.datastructure.Event
import com.goodvibes.multimessenger.usecase.ChatActivityUC
import com.goodvibes.multimessenger.util.ListSingleChatAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var currentChat: Chat
    var listMessage = Collections.synchronizedList(arrayListOf<Message>())
    lateinit var listMessageAdapter: ListSingleChatAdapter
    lateinit var toolbar: Toolbar

    private var isLoadingNewMessages: Boolean = true
    private var numberMessageOnPage: Int = 50
    private var numberLastMessage: Int = 50

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
        toolbar.setNavigationOnClickListener{
            finish()
        }
        activityChatBinding.chatBtnSendMessage.setOnClickListener {
            val messageString = activityChatBinding.chatInputMessage.text.toString()
            if(!messageString.isEmpty()) {
                val dateTime = Date().time
                val message = Message(
                    text = messageString,
                    chatId = currentChat.chatId,
                    date = dateTime.toInt(),
                    time = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale("ru", "ru")).format(dateTime),
                    isMyMessage = true,
                    messenger = currentChat.messenger
                )
                usecase.sendMessage(message) { message_id ->
                    message.id = message_id
                    GlobalScope.launch(Dispatchers.Main) {
                        listMessageAdapter.add(message)
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
        activityChatBinding.listMessage.setOnScrollListener(OnScrollListenerChats())
        usecase.getMessageFromChat(currentChat, 50) { messages ->
            GlobalScope.launch(Dispatchers.Main) {
//                listMessageAdapter.addAll(messages)
                for (message in messages) {
                    Log.d("MM_LOG", "initListMessage: ${message.text}")
                }
                listMessageAdapter.addAll(messages.reversed())
                listMessageAdapter.notifyDataSetChanged()
                isLoadingNewMessages = false
            }
        }
        usecase.startUpdateListener(currentChat) { event ->
            when(event) {
                is Event.NewMessage -> {
                    Log.d("MM_LOG", "${event.message.chatId} == ${currentChat.chatId}")
                    if (event.message.chatId == currentChat.chatId) {
                        Log.d("MM_LOG", "${event.message.chatId} == ${currentChat.chatId} 2")
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("MM_LOG", "${event.message.chatId} == ${currentChat.chatId} 3")
//                            listMessageAdapter.add(event.message)
                            listMessageAdapter.add(event.message)
                            listMessageAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    inner class OnScrollListenerChats : AbsListView.OnScrollListener {
        override fun onScrollStateChanged(recyclerView: AbsListView?, newState: Int) {
        }

        override fun onScroll(view: AbsListView?, firstVisibleItem: Int,
                              visibleItemCount: Int, totalItemCount: Int) {
            if (!isLoadingNewMessages && firstVisibleItem == 0) {
                // (firstVisibleItem + visibleItemCount == totalItemCount)
                isLoadingNewMessages = true
                usecase.getMessageFromChat(currentChat, numberMessageOnPage, numberLastMessage) { messages ->
                    numberLastMessage += numberMessageOnPage
                    isLoadingNewMessages = false
                    GlobalScope.launch(Dispatchers.Main) {
                        for (message in messages.reversed()) {
                            listMessageAdapter.insert(message, 0)
                        }
                        listMessageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}