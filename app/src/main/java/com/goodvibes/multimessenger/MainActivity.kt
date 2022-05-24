package com.goodvibes.multimessenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.autofill.Validators.not
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.*
import android.widget.AbsListView.OnScrollListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.Comparator

import okhttp3.internal.notify

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding : ActivityMainBinding;
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var toolbar: Toolbar
    lateinit var spinner: Spinner
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var folders: ArrayList<String>
    val myDbManager = MyDBManager(this)
    lateinit var useCase: MainActivityUC
    val vk = VK
    val tg = Telegram
    var counter = 0
    lateinit var listChatsAdapter: ListChatsAdapter
    var spinnerInit = false

    lateinit var dialog : SelectFolder
    val dbUseCase = MyDBUseCase(myDbManager)


    private var numberLastChatVK: Int = 0
    private var numberLastChatTG: Int = 0

    private var isLoadingChatVK: Boolean = false
    private var isLoadingChatTG: Boolean = false

    private var numberChatOnPage: Int = 10
    private var currentFolder: Folder = Folder(idAllFolder, "AllChats")
    lateinit var adapter : AdapterView.OnItemSelectedListener

    var mActionMode: ActionMode? = null
    lateinit var callback: ListChatsActionModeCallback

    var allChats = Collections.synchronizedList(mutableListOf<Chat>())

    private var swipeContainer: SwipeRefreshLayout? = null

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

        initSwipeRefresh()
        initMenu()
        initChatsAllAdapter()

        useCase.getCurrentUserVK { user ->
            var viewName = findViewById<TextView>(R.id.drawer_header_username)
            viewName.text = user.firstName + " " + user.lastName

            var userAva = findViewById<ImageView>(R.id.drawer_header_ava)
            Picasso.get().load(user.imgUri).into(userAva)
        }

        folders = myDbManager.getFolders()

        spinner = findViewById(R.id.sp_option)

        callback = ListChatsActionModeCallback()

        spinnerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, folders)
        adapter = CustomItemSelectListener()
        spinner.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()
        spinner.onItemSelectedListener = adapter
        activityMainBinding.listChats.addOnScrollListener(OnScrollListenerChats())

    }

    inner class CustomItemSelectListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            //Log.d("FOLDERS", "OK1")
            currentFolder.name = folders.get(p2)
            allChats.clear()
            listChatsAdapter.notifyDataSetChanged()
            numberLastChatVK = 0
            numberLastChatTG = 0
            if (!isLoadingChatVK && !isLoadingChatTG) {
                useCase.getAllChats(numberChatOnPage, numberLastChatVK, numberLastChatTG) { chats ->
                    GlobalScope.launch(Dispatchers.Main) {
                        val folderUID = myDbManager.getFolderUIDByName(folders.get(p2))
                        val chatsDB = myDbManager.getChatsByFolder(folderUID)

                        val tempChats: MutableList<Chat> = mutableListOf()
                        if (currentFolder.name != "AllChats") {
                            for (item in chatsDB) {
                                for (chat in chats) {
                                    if (chat.chatId == item) {
                                        tempChats.add(chat)
                                    }
                                }
                            }
                        } else {
                            tempChats.addAll(chats)
                        }
                        allChats.addAll(tempChats)
                        allChats.sortWith(ComparatorChats().reversed())
                        listChatsAdapter.notifyDataSetChanged()
                        if (chats[0].messenger == Messengers.VK) {
                            isLoadingChatVK = false
                            numberLastChatVK+=numberChatOnPage
                        } else {
                            isLoadingChatTG = false
                            numberLastChatTG+=numberChatOnPage
                        }
                    }
                }
            }
        }


        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }



    override fun onResume() {
        super.onResume()
        //allChats.clear()
        //numberLastChat = 0
        //getStartChats()
        //Log.d("FOLDERS", "RESUME")
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
            }
            true
        }
    }

    private fun initSwipeRefresh() {
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer?.setOnRefreshListener(OnRefreshListener {
            allChats.clear()
            numberLastChatVK = 0
            numberLastChatTG = 0
            getStartChats()
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initChatsAllAdapter() {
    listChatsAdapter = ListChatsAdapter(this@MainActivity, allChats, this@MainActivity);
    activityMainBinding.listChats.setAdapter(listChatsAdapter);
    useCase.startUpdateListener { event ->
        GlobalScope.launch(Dispatchers.Main) {
            when (event) {
                is Event.NewMessage -> {
                    useCase.getChatByID(
                        event.message.messenger,
                        event.message.chatId
                    ) { chat: Chat ->
                        for (i in allChats.indices) {
                            if (chat.chatId == allChats[i].chatId) {
                                allChats.removeAt(i)
                                break
                            }
                        }
                        chat.lastMessage = event.message
                        allChats.add(0, chat)
                        listChatsAdapter.notifyDataSetChanged()
                    }
                    //Log.d("VK_LOG", "new incoming message: ${event.message}")
                }
            }
        }
    }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getStartChats() {
        swipeContainer?.setRefreshing(true);

        //Log.d("RESUME", "PS")

        //Log.d("FOLDERS", "getStartChats")
        if (!isLoadingChatTG && !isLoadingChatVK) {
            isLoadingChatTG = true
            isLoadingChatVK = true
            useCase.getAllChats(numberChatOnPage, 0, 0) { chats ->
                GlobalScope.launch(Dispatchers.Main) {
                    //Log.d("FOLDERS", "getStartChats 1")


                    val folderUID = myDbManager.getFolderUIDByName(currentFolder.name)
                    val chatsDB = myDbManager.getChatsByFolder(folderUID)

                    val tempChats: MutableList<Chat> = mutableListOf()
                    if (currentFolder.name != "AllChats") {
                        for (item in chatsDB) {
                            for (chat in chats) {
                                if (chat.chatId == item) {
                                    tempChats.add(chat)
                                }
                            }
                        }
                    } else {
                        tempChats.addAll(chats)
                    }
                    allChats.addAll(tempChats)
                    allChats.sortWith(ComparatorChats().reversed())
                    listChatsAdapter.notifyDataSetChanged()
                    swipeContainer?.setRefreshing(false);
                    if (chats[0].messenger == Messengers.VK) {
                        isLoadingChatVK = false
                        numberLastChatVK += numberChatOnPage

                    } else {
                        isLoadingChatTG = false
                        numberLastChatTG += numberChatOnPage

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
        dialog.dismiss()
        mActionMode?.finish()
    }

    private fun addFolderAndMoveChat(chat: Chat) {
        useCase.addFolder(chat)
        dialog.dismiss()
        mActionMode?.finish()
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
                    dialog = SelectFolder(allFolders, foldersAdapter, chat!!, ::moveChatToFolder, ::addFolderAndMoveChat)
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

        @SuppressLint("NotifyDataSetChanged")
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
           super.onScrolled(recyclerView, dx, dy)
           val layoutManager = recyclerView.layoutManager as LinearLayoutManager
           val visibleItemCount = layoutManager.childCount
           val totalItemCount = layoutManager.itemCount
           val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

           if (!isLoadingChatVK &&!isLoadingChatTG &&  (firstVisibleItemPosition + visibleItemCount >= totalItemCount)) {
               isLoadingChatVK = true
               isLoadingChatTG = true
               useCase.getAllChats(numberChatOnPage, numberLastChatVK, numberLastChatTG) { chats ->
                   GlobalScope.launch(Dispatchers.Main) {
                       if (chats[0].messenger == Messengers.VK) {
                           isLoadingChatVK = false
                           numberLastChatVK += numberChatOnPage

                       } else {
                           isLoadingChatTG = false
                           numberLastChatTG += numberChatOnPage

                       }
                       val folderUID = myDbManager.getFolderUIDByName(currentFolder.name)
                       val chatsDB = myDbManager.getChatsByFolder(folderUID)

                       val tempChats: MutableList<Chat> = mutableListOf()
                       if (currentFolder.name != "AllChats") {
                           for (item in chatsDB) {
                               for (chat in chats) {
                                   if (chat.chatId == item) {
                                       tempChats.add(chat)
                                   }
                               }
                           }
                       } else {
                           tempChats.addAll(chats)
                       }
                       allChats.addAll(tempChats)
                       allChats.sortWith(ComparatorChats().reversed())
                       listChatsAdapter.notifyDataSetChanged()
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
