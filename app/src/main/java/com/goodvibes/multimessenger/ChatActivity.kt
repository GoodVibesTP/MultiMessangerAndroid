package com.goodvibes.multimessenger

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
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
    val checkedItems: MutableSet<Long>
    var lastCheckedItemCount = 0
    lateinit var listMessageAdapter: ListSingleChatAdapter
    lateinit var toolbar: Toolbar

    private var isLoadingNewMessages: Boolean = true
    var listMessagesLastLoadedId: Long = 0
    var listMessagesLastLoadedCount: Int = 1
    private var numberMessageOnPage: Int = 50
    private var numberLastMessage: Int = 50

    lateinit var activityChatBinding: ActivityChatBinding;
    lateinit var usecase: ChatActivityUC

    lateinit var progressBarLoadMoreMessages: View

    var actionMode: ActionMode? = null

    private val onItemCheckStateChanged: (MutableSet<Long>) -> Unit

    var modeEditMessage = false
    var currentEditMessageId: Long = 0

    init {
        listMessage = mutableListOf()
        checkedItems = mutableSetOf()
        onItemCheckStateChanged = {  checkState ->
            Log.d("MM_LOG", "callOnItemCheckStateChanged")
            if (lastCheckedItemCount == 0 && checkState.size > 0) {
                actionMode = startActionMode(ChatActivityActionModeCallback(checkedItems))
                Log.d("MM_LOG", "startActionMode")
            }
            else if (lastCheckedItemCount > 0 && checkState.size == 0) {
                actionMode?.finish()
                Log.d("MM_LOG", "finishActionMode")
            }
            Log.d("MM_LOG", "${actionMode?.menu?.findItem(R.id.select_message_menu_reply)}, ${checkState.size}")
            actionMode?.menu?.findItem(R.id.select_message_menu_reply)?.isVisible =
                checkState.size == 1
            actionMode?.menu?.findItem(R.id.select_message_menu_edit)?.isVisible =
                checkState.size == 1 && listMessage.findLast { it.id == checkState.first() }?.isMyMessage ?: false
            lastCheckedItemCount = checkState.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = ActivityChatBinding.inflate(layoutInflater);
        setContentView(activityChatBinding.root)

        currentChat = intent.extras!!.get("Chat") as Chat
        usecase = ChatActivityUC(this)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = currentChat.title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
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
                    time = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale("ru", "ru")).format(
                        dateTime
                    ),
                    isMyMessage = true,
                    read = false,
                    messenger = currentChat.messenger,
                    attachments = null
                )
                if (!modeEditMessage) {
                    usecase.sendMessage(message) { message_id ->
                        message.id = message_id
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("TG_LOG_MSG_IDS", "send msg ${message.chatId} ${message.id} ${message.text}")
                            if (listMessage.lastOrNull { it.id == message_id } == null) {
                                listMessage.add(0, message)
                                Log.d("MM_LOG", "send message ${message.id}")
                                listMessageAdapter.notifyDataSetChanged()
                            }
                            activityChatBinding.chatInputMessage.text.clear()
                        }
                    }
                }
                else {
                    message.id = currentEditMessageId
                    usecase.editMessage(message) { _ ->
                        Log.d("TG_LOG_MSG_IDS", "edit msg ${message.chatId} ${message.id} ${message.text}")
                        GlobalScope.launch(Dispatchers.Main) {
                            val position = listMessage.indexOfFirst { it.id == message.id }
                            if (position >= 0) {
                                listMessage[position] = message
                                listMessageAdapter.notifyDataSetChanged()
                            }

                            activityChatBinding.chatInputMessage.text.clear()
                            listMessageAdapter.notifyDataSetChanged()
                        }
                    }
                    modeEditMessage = false
                }
            }
        }

        initListMessage()
    }


    fun initListMessage() {
        listMessageAdapter = ListSingleChatAdapter(
            this@ChatActivity,
            listMessage,
            checkedItems,
            onItemCheckStateChanged
        )
        activityChatBinding.listMessage.adapter = listMessageAdapter
        activityChatBinding.listMessage.addOnScrollListener(OnScrollListenerChats())
        usecase.getMessageFromChat(currentChat, 50) { messages ->
            GlobalScope.launch(Dispatchers.Main) {
                for (message in messages) {
                    Log.d("TG_LOG_MSG_IDS", "get msg ${message.chatId} ${message.id} ${message.text}")
                }
                listMessage.addAll(messages)
                listMessagesLastLoadedId = listMessage.last().id
                listMessagesLastLoadedCount = messages.size
                listMessageAdapter.notifyDataSetChanged()
                isLoadingNewMessages = false

                markAsReadUntil(listMessage[0].id)
            }
        }
        usecase.startUpdateListener(currentChat) { event ->
            when(event) {
                is Event.DeleteMessage -> {
                    if (event.chat_id == currentChat.chatId) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("TG_LOG_MSG_IDS", "get event delete msg ${event.chat_id} ${event.message_id}")
                            listMessage.removeAll { it.id == event.message_id }
                            listMessageAdapter.notifyDataSetChanged()
                        }
                    }
                }
                is Event.NewMessage -> {
                    if (event.message.chatId == currentChat.chatId) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("TG_LOG_MSG_IDS", "get event new msg ${event.message.chatId} ${event.message.id} ${event.message.text}")
                            if (listMessage.lastOrNull { it.id == event.message.id } == null) {
                                Log.d("MM_LOG", "get message ${event.message.id}")
                                listMessage.add(0, event.message)
                                listMessageAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                is Event.EditMessage -> {
                    if (currentChat.chatId == event.message.chatId) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("TG_LOG_MSG_IDS", "get event edit msg ${event.message.chatId} ${event.message.id} ${event.message.text}")
                            val position = listMessage.indexOfFirst { it.id == event.message.id }
                            if (position >= 0) {
                                listMessage[position] = event.message
                                listMessageAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                is Event.EditMessageContent -> {
                    if (currentChat.chatId == event.chat_id) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("TG_LOG_MSG_IDS", "get event edit msg ${event.chat_id} ${event.message_id} ${event.text}")
                            val messageToEdit = listMessage.firstOrNull { it.id == event.message_id }
                            messageToEdit?.text = event.text
                            listMessageAdapter.notifyDataSetChanged()
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
                    isLoadingNewMessages = true
                    val listMessageLastId = listMessage.last().id
                    if (listMessagesLastLoadedId != listMessageLastId || listMessagesLastLoadedCount > 0) {
                        progressBarLoadMoreMessages.visibility = View.VISIBLE
                        usecase.getMessageFromChat(
                            chat = currentChat,
                            count = numberMessageOnPage,
                            first_msg_id = listMessageLastId
                        ) { messages ->
                            numberLastMessage += numberMessageOnPage
                            GlobalScope.launch(Dispatchers.Main) {
                                listMessage.addAll(messages)
                                listMessagesLastLoadedId = listMessage.last().id
                                listMessagesLastLoadedCount = messages.size
                                isLoadingNewMessages = false
                                listMessageAdapter.notifyDataSetChanged()
                            }
                            progressBarLoadMoreMessages.visibility = View.GONE
                        }
                    }
                }
            }

            val firstCompletelyVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (firstCompletelyVisibleItem > 0) {
                if (listMessage[firstCompletelyVisibleItem].id > currentChat.inRead) {
                    markAsReadUntil(listMessage[firstCompletelyVisibleItem].id)
                }
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

    inner class ChatActivityActionModeCallback(
        val checkedItems: MutableSet<Long>
    ) : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.select_message_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.select_message_menu_edit -> {
                    val messageToEdit = listMessage.firstOrNull { it.id == checkedItems.first() }
                    checkedItems.clear()
                    lastCheckedItemCount = 0
                    actionMode?.finish()
                    listMessageAdapter.notifyDataSetChanged()

                    activityChatBinding.chatInputMessage.setText(messageToEdit?.text ?: "")
                    modeEditMessage = true
                    currentEditMessageId = messageToEdit?.id ?: 0
                }
                R.id.select_message_menu_reply -> {

                }
                R.id.select_message_menu_delete -> {
                    Log.d("TG_LOG_MSG_IDS", "delete msg ${currentChat.chatId} ${checkedItems.toSortedSet().joinToString(separator = " ")}")
                    usecase.deleteMessages(currentChat, checkedItems.toList())
                    listMessage.removeAll { checkedItems.contains(it.id) }
                    checkedItems.clear()
                    lastCheckedItemCount = 0
                    actionMode?.finish()
                    if (currentChat.lastMessage?.id != listMessage[0].id) {
                        currentChat.lastMessage = listMessage[0]
                    }
                    listMessageAdapter.notifyDataSetChanged()
                }
                R.id.select_message_menu_resend -> {

                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
        }

    }
}
