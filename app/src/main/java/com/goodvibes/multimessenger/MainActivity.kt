package com.goodvibes.multimessenger

import android.app.Activity
import android.os.Bundle

import com.goodvibes.multimessenger.databinding.ActivityMainBinding
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Messengers

class MainActivity : Activity() {
    lateinit var activityMainBinding : ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(activityMainBinding.root)

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
        //activityMainBinding.listChats.setClickable(true);

    }
}
