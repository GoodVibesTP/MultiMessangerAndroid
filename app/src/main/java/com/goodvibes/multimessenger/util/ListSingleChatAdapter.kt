package com.goodvibes.multimessenger.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers

class ListSingleChatAdapter: ArrayAdapter<Message> {
    public constructor(ctx: Context, messages: MutableList<Message>) :
            super(ctx, R.layout.list_item_chats, messages){}

    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView_ = convertView;
        val message = getItem(position);
        if (convertView_ == null) {
            convertView_ = LayoutInflater.from(context).inflate(R.layout.list_item_message,
                parent,false);
        }

        val layoutMessage: View
        val textViewTextMessage: View
        val textViewTime: View
        val layoutMessageToSetInvisible: View
        if (message!!.isMyMessage) {
            layoutMessage = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_my_layout)
            textViewTextMessage = layoutMessage.findViewById<TextView>(R.id.chat_my_message)
            textViewTime = layoutMessage.findViewById<TextView>(R.id.chat_my_message_time)
            layoutMessageToSetInvisible = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_other_user_layout)
        } else {
            layoutMessage = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_other_user_layout)
            textViewTextMessage = layoutMessage.findViewById<TextView>(R.id.chat_other_user_message)
            textViewTime = layoutMessage.findViewById<TextView>(R.id.chat_other_user_message_time)
            layoutMessageToSetInvisible = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_my_layout)
        }

        textViewTextMessage.setText(message.text)
        textViewTime.setText(message.time)
        layoutMessageToSetInvisible.visibility = View.GONE

        return convertView_;
    }

}