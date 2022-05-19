package com.goodvibes.multimessenger

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
                    messenger = currentChat.messenger
                )
                usecase.sendMessage(message) { message_id ->
                    message.id = message_id
                    GlobalScope.launch(Dispatchers.Main) {
                        listMessage.add(0, message)
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
            }
        }
        usecase.markAsRead(currentChat) {
            if (it == 1) {
                Toast.makeText(
                    this,
                    "messages marked as readed",
                    Toast.LENGTH_LONG
                ).show()
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
                            listMessage.add(0, event.message)
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
        }
    }
}
