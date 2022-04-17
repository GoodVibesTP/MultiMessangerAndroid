package com.goodvibes.multimessenger

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.goodvibes.multimessenger.databinding.ActivityMainBinding
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.dialog.SelectFolder
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK
import com.goodvibes.multimessenger.usecase.MainActivityUC
import com.goodvibes.multimessenger.util.ListFoldersAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding : ActivityMainBinding;
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var toolbar: Toolbar
    lateinit var useCase: MainActivityUC
    val vk = VK
    val tg = Telegram
    lateinit var listChatsAdapter: ListChatsAdapter

    private var numberLastChatVK: Int = 0
    private var isLoadingChatVK: Boolean = false
    private var numberChatOnPage: Int = 20

    var mActionMode: ActionMode? = null
    lateinit var callback: ListChatsActionModeCallback

    var allChats: MutableList<Chat> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        vk.initClientWithActivity(this)
        tg.initClientWithActivity(this)
        useCase = MainActivityUC(this, vk, tg)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(activityMainBinding.root)

       if (!useCase.isLogin()) {
           val intent = Intent(this, AuthorizationActivity::class.java)
           startActivity(intent)
       }

        initMenu()
        initChatsAllAdapter()

        callback = ListChatsActionModeCallback()

        tg.getMessagesFromChat(197730632, 100, 0) { messages ->
            for (message in messages) {
                Log.d("MM_LOG", message.toString())
            }
        }

        tg.sendMessage(197730632, "newMessage") { message ->
            Log.d("MM_LOG", "new Message: $message")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initMenu(){
        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_LONG).show()
                R.id.nav_settings -> Toast.makeText(applicationContext, "Clicked settings", Toast.LENGTH_LONG).show()
                R.id.nav_chto_to -> Toast.makeText(applicationContext, "Clicked chtoto", Toast.LENGTH_LONG).show()
            }
            true
        }
    }

//TODO: ТУТ ОПРЕДЕЛЕННО НУЖНО ВЫНЕСТИ В ФУНКЦИЮ ИНИН КАЛЛБЭКА
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initChatsAllAdapter() {
    allChats = mutableListOf()
        useCase.getAllChats(10, 0) { chats ->
            GlobalScope.launch(Dispatchers.Main) {
                numberLastChatVK = numberChatOnPage
                allChats.addAll(chats)
                listChatsAdapter = ListChatsAdapter(this@MainActivity, allChats);
                activityMainBinding.listChats.setAdapter(listChatsAdapter);
                activityMainBinding.listChats.setOnItemLongClickListener { parent, view, position, id ->
                    if (mActionMode != null) {
                        false
                    }
                    view.isSelected = true
                    callback.setClickedView(position)
                    mActionMode = startActionMode(callback)!!
                    true
                }
                activityMainBinding.listChats.setOnScrollListener(OnScrollListenerChats())
//                for (chat in chats) {
//                    Log.d("MM_LOG", chat.toString())
//                }
            }
        }

//        vk.startUpdateListener { event ->
//            when(event) {
//                is Event.NewMessage -> {
//                    Log.d("VK_LOG", "new incoming message: ${event.message}")
//                    for (i in allChats.indices) {
//                        if (allChats[i].chatId == event.message.chatId) {
//                            allChats[i].lastMessage = event.message
//                            val updatedChat = allChats!!.removeAt(i)
//                            allChats.add(0, updatedChat)
//                            break
//                        }
//                    }
//
//                    GlobalScope.launch(Dispatchers.Main) {
//                        var listChatsAdapter: ListChatsAdapter = ListChatsAdapter(this@MainActivity, allChats);
//                        activityMainBinding.listChats.setAdapter(listChatsAdapter);
//                        activityMainBinding.listChats.setOnItemLongClickListener { parent, view, position, id ->
//                            if (mActionMode != null) {
//                                false
//                            }
//                            mActionMode = startSupportActionMode(callback)!!
//                            true
//                        }
//                        view.isSelected = true
//                        callback.setClickedView(position)
//                        mActionMode = startActionMode(callback)!!
//
//                        true
//                    }
//                }
//            }
//        }
//
//        vk.getChatById(231958258) { chat ->
//            Log.d("MM", chat.toString())
//        }
//
//        val tg = Telegram(this)
//        tg.client.send(TdApi.GetAuthorizationState(), tg.UpdateHandler())
//
//        tg.getAllChats(10) { chats ->
//            allChats.addAll(chats)
//            allChats.sortBy { chat -> - chat.lastMessage!!.date }
//            GlobalScope.launch(Dispatchers.Main) {
//                var listChatsAdapter: ListChatsAdapter = ListChatsAdapter(this@MainActivity, allChats);
//                activityMainBinding.listChats.setAdapter(listChatsAdapter);
//                activityMainBinding.listChats.setOnItemLongClickListener { parent, view, position, id ->
//                    if (mActionMode != null) {
//                        false
//                    }
//                    mActionMode = startSupportActionMode(callback)!!
//                    true
//                }
//            }
//        }
//
//        tg.getChatById(197730632) { chat ->
//            android.util.Log.d("MM", chat.toString())
//        }
//
//        tg.getMessagesFromChat(197730632, 100, 0) { messages ->
//            for (message in messages) {
//                Log.d("MM", message.toString())
//            }
//        }
//
//        tg.startUpdateListener { event ->
//            when(event) {
//                is Event.NewMessage -> {
//                    Log.d("VK_LOG", "new incoming message: ${event.message}")
//                    for (i in allChats.indices) {
//                        if (allChats[i].chatId == event.message.chatId) {
//                            allChats[i].lastMessage = event.message
//                            val updatedChat = allChats!!.removeAt(i)
//                            allChats.add(0, updatedChat)
//                            break
//                        }
//                    }
//
////                    for (chat in allChats) {
////                        Log.d("MM", "${chat.title} -> ${chat.lastMessage!!.date}")
////                    }
//                    GlobalScope.launch(Dispatchers.Main) {
//                        var listChatsAdapter: ListChatsAdapter = ListChatsAdapter(this@MainActivity, allChats);
//                        activityMainBinding.listChats.setAdapter(listChatsAdapter);
//                        activityMainBinding.listChats.setOnItemLongClickListener { parent, view, position, id ->
//                            if (mActionMode != null) {
//                                false
//                            }
//                            mActionMode = startSupportActionMode(callback)!!
//                            true
//                        }
//                    }
//                }
//            }
//        }
//
    }


    private fun deleteChat(chat: Chat) {
        useCase.deleteChat(chat)
    }

    private fun moveChatToFolder(chat: Chat) : Unit {
        useCase.moveChatToFolder(chat)
    }

    private fun addFolderAndMoveChat(chat: Chat) {
        useCase.addFolder(chat)
    }

    inner class ListChatsActionModeCallback : ActionMode.Callback {
        var mClickedViewPosition: Int? = null

        fun setClickedView(view: Int?) {
            mClickedViewPosition = view
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.select_chat_menu, menu)
            mode?.setTitle("choose your option")

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.select_chat_menu_delete -> {
                    val chat = this@MainActivity.listChatsAdapter.getItem(this.mClickedViewPosition!!)
                    deleteChat(chat!!)
                    mode?.finish()
                    return true
                }
                R.id.select_chat_mov_to_folder -> {
                    val allFolders = useCase.getAllFolders()

                    val foldersAdapter = ListFoldersAdapter(this@MainActivity, allFolders)
                    val chat = this@MainActivity.listChatsAdapter.getItem(this.mClickedViewPosition!!)
                    val dialog = SelectFolder(allFolders, foldersAdapter, chat!!, ::moveChatToFolder, ::addFolderAndMoveChat)
                    val manager = supportFragmentManager
                    dialog.show(manager, "Select folder")
                    return true
                }
            }
            return false;
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mActionMode = null;
        }
    }

    inner class OnScrollListenerChats : OnScrollListener {
        override fun onScrollStateChanged(recyclerView: AbsListView?, newState: Int) {
        }

        override fun onScroll(view: AbsListView?, firstVisibleItem: Int,
                              visibleItemCount: Int, totalItemCount: Int) {
            if (!isLoadingChatVK &&  (firstVisibleItem + visibleItemCount == totalItemCount)) {
                isLoadingChatVK = true
                useCase.getAllChats(10, numberLastChatVK, vk) {chats ->
                    numberLastChatVK += numberChatOnPage
                    isLoadingChatVK = false
                    listChatsAdapter.addAll(chats)
                }

            }
        }

    }
}



