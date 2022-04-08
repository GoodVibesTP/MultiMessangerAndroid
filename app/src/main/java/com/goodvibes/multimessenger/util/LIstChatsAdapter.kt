package com.goodvibes.multimessenger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Messengers
import kotlinx.coroutines.CoroutineScope

class ListChatsAdapter: ArrayAdapter<Chat> {
    public constructor(ctx: Context, chats: List<Chat>) :
            super(ctx, R.layout.list_item_chats, chats){}

    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView_ = convertView;
        val chat = getItem(position);
        if (convertView_ == null) {
            convertView_ = LayoutInflater.from(context).inflate(R.layout.list_item_chats, parent,false);
        }

        val imageViewAva: ImageView = convertView_!!.findViewById(R.id.chat_all_image);
        val imageViewMessenger : ImageView = convertView_!!.findViewById(R.id.chat_all_messenger_img);
        val textViewTitle: TextView = convertView_!!.findViewById(R.id.chat_all_title);
        val textViewLastMessage: TextView = convertView_!!.findViewById(R.id.last_message);
        imageViewAva.setImageResource(chat!!.img);
        if (chat!!.messenger == Messengers.VK) {
            imageViewMessenger.setImageResource(R.mipmap.tg_icon);
        } else {
            imageViewMessenger.setImageResource(R.mipmap.tg_icon)
        }

        textViewTitle.setText(chat!!.title);
        textViewLastMessage.setText(chat!!.lastMessage!!.text);

        return convertView_;
    }

}