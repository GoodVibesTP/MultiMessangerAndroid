package com.goodvibes.multimessenger

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.databinding.ActivityChatBinding
import com.goodvibes.multimessenger.datastructure.Event
import com.goodvibes.multimessenger.usecase.ChatActivityUC
import com.goodvibes.multimessenger.util.ListSingleChatAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class ChatActivity : AppCompatActivity() {
    lateinit var currentChat: Chat
    var listMessage = Collections.synchronizedList(arrayListOf<Message>())
    lateinit var listMessageAdapter: ListSingleChatAdapter
    lateinit var toolbar: Toolbar

    private var isLoadingNewMessages: Boolean = true
    private var numberMessageOnPage: Int = 50
    private var numberLastMessage: Int = 50

    lateinit var activityChatBinding: ActivityChatBinding;
    lateinit var usecase: ChatActivityUC

    lateinit var progressBarLoadMoreMessages: View

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
        progressBarLoadMoreMessages = findViewById(R.id.progressbar)
        progressBarLoadMoreMessages.visibility = View.GONE

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
                    read = false,
                    messenger = currentChat.messenger
                )
                usecase.sendMessage(message) { message_id ->
                    message.id = message_id
                    GlobalScope.launch(Dispatchers.Main) {
                        if (listMessage.lastOrNull { it.id == message_id } == null) {
                            listMessage.add(0, message)
                            Log.d("MM_LOG", "send message ${message.id}")
                            listMessageAdapter.notifyDataSetChanged()
                        }
                        activityChatBinding.chatInputMessage.text.clear()
                    }
                }
            }
        }

        initListMessage()
    }


    fun initListMessage() {
        listMessage = mutableListOf()
        listMessageAdapter = ListSingleChatAdapter(this@ChatActivity, listMessage)
        activityChatBinding.listMessage.adapter = listMessageAdapter
        activityChatBinding.listMessage.addOnScrollListener(OnScrollListenerChats())
        usecase.getMessageFromChat(currentChat, 50) { messages ->
            GlobalScope.launch(Dispatchers.Main) {
                for (message in messages) {
                    Log.d("MM_LOG", "initListMessage: ${message.text}")
                }
                listMessage.addAll(messages)
                listMessageAdapter.notifyDataSetChanged()
                isLoadingNewMessages = false

                markAsReadUntil(listMessage[0].id)
            }
        }
        usecase.startUpdateListener(currentChat) { event ->
            when(event) {
                is Event.NewMessage -> {
                    if (event.message.chatId == currentChat.chatId) {
                        GlobalScope.launch(Dispatchers.Main) {
                            if (listMessage.lastOrNull { it.id == event.message.id } == null) {
                                Log.d("MM_LOG", "get message ${event.message.id}")
                                listMessage.add(0, event.message)
                                listMessageAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                is Event.ReadIngoingUntil -> {
                    if (currentChat.chatId == event.chat_id) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                this@ChatActivity,
                                "ingoing messages marked as read",
                                Toast.LENGTH_LONG
                            ).show()
                            currentChat.inRead = event.message_id
                            for (message in listMessage) {
                                if (!message.isMyMessage && message.id <= event.message_id) {
                                    if (message.read) {
                                        break
                                    } else {
                                        message.read = true
                                    }
                                }
                            }
                            listMessageAdapter.notifyDataSetChanged()
                        }
                    }
                }
                is Event.ReadOutgoingUntil -> {
                    if (currentChat.chatId == event.chat_id) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                this@ChatActivity,
                                "outgoing messages marked as read",
                                Toast.LENGTH_LONG
                            ).show()
                            currentChat.inRead = event.message_id
                            for (message in listMessage) {
                                if (message.isMyMessage && message.id <= event.message_id) {
                                    if (message.read) {
                                        break
                                    } else {
                                        message.read = true
                                    }
                                }
                            }
                            listMessageAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    inner class OnScrollListenerChats : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            if (!isLoadingNewMessages && firstVisibleItemPosition >= 0) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                    progressBarLoadMoreMessages.visibility = View.VISIBLE
                    isLoadingNewMessages = true
                    usecase.getMessageFromChat(
                        chat = currentChat,
                        count = numberMessageOnPage,
                        first_msg = numberLastMessage,
                        first_msg_id = listMessage.last().id
                    ) { messages ->
                        numberLastMessage += numberMessageOnPage
                        isLoadingNewMessages = false
                        GlobalScope.launch(Dispatchers.Main) {
                            listMessage.addAll(messages)
                            listMessageAdapter.notifyDataSetChanged()
                        }
                        progressBarLoadMoreMessages.visibility = View.GONE
                    }
                }
            }

            val firstCompletelyVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (listMessage[firstCompletelyVisibleItem].id > currentChat.inRead) {
                markAsReadUntil(listMessage[firstCompletelyVisibleItem].id)
            }
        }
    }

    private fun markAsReadUntil(message_id: Long) {
        val unreadMessageIds: MutableList<Long> = mutableListOf()

        for (message in listMessage) {
            if (!message.isMyMessage && !message.read) {
                unreadMessageIds.add(message.id)
            }
        }
        Log.d("MM_LOG", "initListMessage, read message: ${currentChat.inRead} $message_id ${unreadMessageIds.size}")

        if (unreadMessageIds.size > 0) {
            usecase.markAsRead(
                chat = currentChat,
                message_ids = unreadMessageIds
            ) {
                if (it == 1) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@ChatActivity,
                            "messages marked as read",
                            Toast.LENGTH_LONG
                        ).show()
                        currentChat.inRead = message_id
                        for (message in listMessage) {
                            if (!message.isMyMessage && message.id <= message_id) {
                                if (message.read) {
                                    break
                                } else {
                                    message.read = true
                                }
                            }
                        }
                        listMessageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
