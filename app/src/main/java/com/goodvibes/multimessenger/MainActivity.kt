package com.goodvibes.multimessenger

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout


import com.goodvibes.multimessenger.databinding.ActivityMainBinding
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Messengers
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding : ActivityMainBinding;
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(activityMainBinding.root)

        initMenu()



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
}
