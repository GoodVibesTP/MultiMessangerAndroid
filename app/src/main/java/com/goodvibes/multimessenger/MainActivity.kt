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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testdb3.db.MyDBManager
import com.goodvibes.multimessenger.databinding.ActivityMainBinding
import com.goodvibes.multimessenger.datastructure.*
import com.goodvibes.multimessenger.db.MyDBUseCase
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


    val dbUseCase = MyDBUseCase(myDbManager)


    private var numberLastChat: Int = 0
    private var isLoadingChatVK: Boolean = false
    private var numberChatOnPage: Int = 10
    private var currentFolder: Folder = Folder(idAllFolder, "AllChats")

    var mActionMode: ActionMode? = null
    lateinit var callback: ListChatsActionModeCallback

    var allChats: MutableList<Chat> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        vk.init(this)
        tg.init(this)
        useCase = MainActivityUC(this, vk, tg, dbUseCase)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(activityMainBinding.root)

       if (!useCase.isLogin()) {
           val intent = Intent(this, AuthorizationActivity::class.java)
           startActivity(intent)
       }

        myDbManager.openDb()
        dbUseCase.addPrimaryFolders()

        initMenu()
        initChatsAllAdapter()

        callback = ListChatsActionModeCallback()
    }

    override fun onResume() {
        super.onResume()
        allChats.clear()
        numberLastChat = 0
        getStartChats()
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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initChatsAllAdapter() {
    allChats = mutableListOf()
    listChatsAdapter = ListChatsAdapter(this@MainActivity, allChats, this@MainActivity);
    activityMainBinding.listChats.setAdapter(listChatsAdapter);

//        vk.startUpdateListener { event ->
//            when(event) {
//                is Event.NewMessage -> {
//                    Log.d("VK_LOG", "new incoming message: ${event.message}")
//                    for (i in allChats.indices) {
//                        if (allChats[i].chatId == event.message.chatId) {
//                            allChats[i].lastMessage = event.message
//                            val updatedChat = allChats.removeAt(i)
//                            allChats.add(0, updatedChat)
//                            break
//                        }
//                    }
//
//                    var listChatsAdapter: ListChatsAdapter = ListChatsAdapter(this@MainActivity,
//                        allChats, this@MainActivity);
//                    activityMainBinding.listChats.setAdapter(listChatsAdapter);
//                }
//            }
//        }
    }

    private fun getStartChats() {
        useCase.getAllChats(numberChatOnPage, 0) { chats ->
            GlobalScope.launch(Dispatchers.Main) {
                numberLastChat = numberChatOnPage
                allChats.addAll(chats)
                allChats.sortWith(ComparatorChats().reversed())
                listChatsAdapter.notifyDataSetChanged()

                activityMainBinding.listChats.addOnScrollListener(OnScrollListenerChats())

                for (item in chats) {
                    dbUseCase.addChatsToPrimaryFolderIfNotExist(item)
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
                    val chat = this@MainActivity.listChatsAdapter.chats[this.mClickedViewPosition!!]
                    deleteChat(chat!!)
                    mode?.finish()
                    return true
                }
                R.id.select_chat_mov_to_folder -> {
                    val allFolders = useCase.getAllFolders()

                    val foldersAdapter = ListFoldersAdapter(this@MainActivity, allFolders)
                    val chat = this@MainActivity.listChatsAdapter.chats[this.mClickedViewPosition!!]
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

    inner class OnScrollListenerChats : RecyclerView.OnScrollListener()  {
       override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
           super.onScrollStateChanged(recyclerView, newState)
       }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
           super.onScrolled(recyclerView, dx, dy)
           val layoutManager = recyclerView.layoutManager as LinearLayoutManager
           val visibleItemCount = layoutManager.childCount
           val totalItemCount = layoutManager.itemCount
           val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
           if (!isLoadingChatVK &&  (firstVisibleItemPosition + visibleItemCount >= totalItemCount)) {
               isLoadingChatVK = true
               useCase.getAllChats(numberChatOnPage, numberLastChat) {chats ->
                   numberLastChat += numberChatOnPage
                   isLoadingChatVK = false
                   allChats.addAll(chats)
                   allChats.sortWith(ComparatorChats().reversed())
                   listChatsAdapter.notifyDataSetChanged()
                   for (item in chats) {
                       dbUseCase.addChatsToPrimaryFolderIfNotExist(item)
                   }
               }
           }
       }
   }

   class ComparatorChats: Comparator<Chat> {
       override fun compare(p0: Chat?, p1: Chat?): Int {
           if (p0 == null || p1 == null) return 0
           else return p0.lastMessage!!.date.compareTo(p1.lastMessage!!.date)
       }
   }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDB()
    }
}
