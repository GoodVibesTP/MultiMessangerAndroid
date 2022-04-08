package com.goodvibes.multimessenger

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout


import com.goodvibes.multimessenger.databinding.ActivityMainBinding
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Messengers
import com.google.android.material.navigation.NavigationView

public class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding : ActivityMainBinding;
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var toolbar: Toolbar
    var mActionMode: ActionMode? = null
    lateinit var callback: ListChatsActionModeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(activityMainBinding.root)

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
                R.id.nav_home -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_LONG).show()
                R.id.nav_settings -> Toast.makeText(applicationContext, "Clicked settings", Toast.LENGTH_LONG).show()
                R.id.nav_chto_to -> Toast.makeText(applicationContext, "Clicked chtoto", Toast.LENGTH_LONG).show()
            }
            true
        }
    }


    private fun initChatsAllAdapter() {
        var img = arrayOf<Int>(R.drawable.kotik, R.drawable.kotik, R.drawable.kotik);
        var title = arrayListOf<String>("Sergey Kust1", "Sergey Kust2", "Sergey Kust3");
        var lastMessage = arrayListOf<String>("Hello", "how are you", "fsdgdf");
        var messangerType = arrayListOf<Messengers>(Messengers.VK, Messengers.TELEGRAM, Messengers.TELEGRAM);

        var chats = arrayListOf<Chat>();
        for(i in 0..2){
            chats += Chat(img[i], title[i], lastMessage[i], messangerType[i]);
        }

        var listChatsAdapter: ListChatsAdapter = ListChatsAdapter(this, chats);
        activityMainBinding.listChats.setAdapter(listChatsAdapter);
        activityMainBinding.listChats.setOnItemLongClickListener { parent, view, position, id ->
            if (mActionMode != null) {
                false
            }
            mActionMode = startSupportActionMode(callback)!!
            true
        }
    }


    inner class ListChatsActionModeCallback : ActionMode.Callback {
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
                    mode?.finish()
                    return true
                }
            }
            return false;
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mActionMode = null;
        }
    }

}



