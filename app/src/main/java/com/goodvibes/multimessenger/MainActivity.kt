package com.goodvibes.multimessenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.testdb3.db.MyDBManager
import com.goodvibes.multimessenger.databinding.ActivityMainBinding
import com.goodvibes.multimessenger.datastructure.*
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
    val myDbManager = MyDBManager(this)
    lateinit var useCase: MainActivityUC
    val vk = VK
    val tg = Telegram
    lateinit var listChatsAdapter: ListChatsAdapter


    private var numberLastChatVK: Int = 0
    private var isLoadingChatVK: Boolean = false
    private var numberChatOnPage: Int = 10
    private var currentFolder: Folder = Folder(idAllFolder, "AllChats")

    var mActionMode: ActionMode? = null
    lateinit var callback: ListChatsActionModeCallback

    var allChats: MutableList<Chat> = mutableListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        vk.init(this)
        tg.init(this)
        useCase = MainActivityUC(this, vk, tg)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(activityMainBinding.root)

       if (!useCase.isLogin()) {
           val intent = Intent(this, AuthorizationActivity::class.java)
           startActivity(intent)
       }

        // TODO:
        myDbManager.openDb()
        myDbManager.addFolderToDB("VK", -3)
        myDbManager.addFolderToDB("TELEGRAM", -2)

        initMenu()
        initChatsAllAdapter()

        callback = ListChatsActionModeCallback()
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
                R.id.nav_home -> {
                    Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_LONG).show()
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, AuthorizationActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_chto_to -> Toast.makeText(applicationContext, "Clicked chtoto", Toast.LENGTH_LONG).show()
            }
            true
        }
    }

//TODO: ТУТ ОПРЕДЕЛЕННО НУЖНО ВЫНЕСТИ В ФУНКЦИЮ ИНИН КАЛЛБЭКА
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initChatsAllAdapter() {
    allChats = mutableListOf()
        useCase.getAllChats(numberChatOnPage, 0) { chats ->
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
                activityMainBinding.listChats.setOnItemClickListener{parent, view, position, id ->
                    Toast.makeText(this@MainActivity, position.toString(),
                        Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    val chat = listChatsAdapter.getItem(position)
                    intent.putExtra("Chat", chat)
                    startActivity(intent)
                }

                myDbManager.openDb()
                for (item in chats) {
                    myDbManager.addChatToDB(item.title, item.chatId)
                    var folderUID = 0
                    if (item.messenger == Messengers.VK) {
                        folderUID = -3
                    } else if (item.messenger == Messengers.TELEGRAM) {
                        folderUID = -2
                    }

                    myDbManager.addChatToFolder(item.chatId, folderUID)
                    Log.d("low", "Successfully add new chat: ${item.title}!")
                }

               // val folders = myDbManager.getFolders()

//                activityMainBinding.include.spOption.setOnTouchListener(
//                    View.OnTouchListener { view, motionEvent ->
//                        when (motionEvent.action){
//                            MotionEvent.ACTION_DOWN -> {
//                                Toast.makeText(this@MainActivity, "DOWN1", Toast.LENGTH_LONG).show()
//                            }
//                            MotionEvent.ACTION_UP -> {
//                                Toast.makeText(this@MainActivity, "UP1", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                        return@OnTouchListener true
//                    }
//                )

                val getFolder = myDbManager.getFolders()

                val options: ArrayList<String> = getFolder
                var adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, options)
                activityMainBinding.include.spOption.adapter = adapter

                activityMainBinding.include.spOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        // val options = arrayOf("Папка 1", "Папка 2", "Папка 3")
                      //  Toast.makeText(this@MainActivity, "DOWN", Toast.LENGTH_LONG).show()
                        //activityMainBinding.include.optionName.text = options.get(p2)
                        adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, myDbManager.getFolders())
                        adapter.notifyDataSetChanged()

                        val folderUID = myDbManager.getFolderUIDByName(options.get(p2))
                        val chatsDB = myDbManager.getChatsByFolder(folderUID)

                        val tempChats : MutableList<Chat> = mutableListOf()

                        for (item in chatsDB) {
                            for (chat in allChats) {
                                if (chat.chatId == item) {
                                    tempChats.add(chat)
                                }
                            }
                        }

                        // listChatsAdapter.notifyDataSetChanged()
                        listChatsAdapter.clear()
                        numberLastChatVK = tempChats.size
                        listChatsAdapter.addAll(tempChats)
                        currentFolder.folderId = folderUID

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                       // Toast.makeText(this@MainActivity, "UP", Toast.LENGTH_LONG).show()
                        activityMainBinding.include.optionName.text = "Выберите папку"
                    }

                }

                //TODO: activityMainBinding.options
            }
        }

        vk.startUpdateListener { event ->
            when(event) {
                is Event.NewMessage -> {
                    Log.d("VK_LOG", "new incoming message: ${event.message}")
                    for (i in allChats.indices) {
                        if (allChats[i].chatId == event.message.chatId) {
                            allChats[i].lastMessage = event.message
                            val updatedChat = allChats.removeAt(i)
                            allChats.add(0, updatedChat)
                            break
                        }
                    }

                    var listChatsAdapter: ListChatsAdapter = ListChatsAdapter(this@MainActivity, allChats);
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
                }
            }
        }
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
                useCase.getAllChats(numberChatOnPage, numberLastChatVK) {chats ->

                    isLoadingChatVK = false

                    // TODO: comment lower

                    myDbManager.openDb()
                    for (item in chats) {
                        myDbManager.addChatToDB(item.title, item.chatId)
                        var folderUID = 0
                        if (item.messenger == Messengers.VK) {
                            folderUID = -3
                        } else if (item.messenger == Messengers.TELEGRAM) {
                            folderUID = -2
                        }

                        myDbManager.addChatToFolder(item.chatId, folderUID)
                        Log.d("low", "Successfully add new chat: ${item.title}!")
                    }
                        // TODO: get folder of chat from db item.folder = chatdb.folder

                        val chatsDB = myDbManager.getChatsByFolder(currentFolder.folderId)

                        val tempChats : MutableList<Chat> = mutableListOf()

                        for (item in chatsDB) {
                            for (chat in chats) {
                                if (chat.chatId == item) {
                                    tempChats.add(chat)
                                }
                            }
                        }

                        listChatsAdapter.addAll(tempChats)
                        numberLastChatVK += tempChats.size

                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDB()
    }
}
