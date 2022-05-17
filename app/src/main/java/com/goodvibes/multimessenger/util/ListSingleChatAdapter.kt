package com.goodvibes.multimessenger.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Message


class ListSingleChatAdapter(
    ctx: Context,
    val messages: MutableList<Message>
): RecyclerView.Adapter<ListSingleChatAdapter.ViewHolder>() {
    val inflater = LayoutInflater.from(ctx)

//    public constructor(ctx: Context, messages: MutableList<Message>) :
//            super(ctx, R.layout.list_item_chats, messages){}
//
//    public fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var convertView_ = convertView;
//        val message = getItem(position);
//        if (convertView_ == null) {
//            convertView_ = LayoutInflater.from(context).inflate(R.layout.list_item_message,
//                parent,false);
//        }
//
//        val layoutMessage: View
//        val textViewTextMessage: View
//        val textViewTime: View
//        val layoutMessageToSetInvisible: View
//        if (message!!.isMyMessage) {
//            layoutMessage = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_my_layout)
//            textViewTextMessage = layoutMessage.findViewById<TextView>(R.id.chat_my_message)
//            textViewTime = layoutMessage.findViewById<TextView>(R.id.chat_my_message_time)
//            layoutMessageToSetInvisible = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_other_user_layout)
//        } else {
//            layoutMessage = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_other_user_layout)
//            textViewTextMessage = layoutMessage.findViewById<TextView>(R.id.chat_other_user_message)
//            textViewTime = layoutMessage.findViewById<TextView>(R.id.chat_other_user_message_time)
//            layoutMessageToSetInvisible = convertView_!!.findViewById<ConstraintLayout>(R.id.chat_my_layout)
//        }
//
//        textViewTextMessage.setText(message.text)
//        textViewTime.setText(message.time)
//        layoutMessageToSetInvisible.visibility = View.GONE
//
//        return convertView_;
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListSingleChatAdapter.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.list_item_message, parent, false))
    }

    override fun onBindViewHolder(
        holder: ListSingleChatAdapter.ViewHolder,
        position: Int
    ) {
        val message = messages[position]
        if (message.isMyMessage) {
            holder.layoutMessageOutgoing.visibility = View.VISIBLE
            holder.textViewTextMessageOutgoing.text = message.text
            holder.textViewTimeOutgoing.text = message.time
            holder.layoutMessageIngoing.visibility = View.GONE
        } else {
            holder.layoutMessageIngoing.visibility = View.VISIBLE
            holder.textViewTextMessageIngoing.text = message.text
            holder.textViewTimeIngoing.text = message.time
            holder.layoutMessageOutgoing.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val layoutMessageIngoing: ConstraintLayout
        internal val layoutMessageOutgoing: ConstraintLayout
        internal val textViewTextMessageIngoing: TextView
        internal val textViewTextMessageOutgoing: TextView
        internal val textViewTimeIngoing: TextView
        internal val textViewTimeOutgoing: TextView

        init {
            layoutMessageIngoing = view.findViewById(R.id.chat_other_user_layout)
            textViewTextMessageIngoing = view.findViewById(R.id.chat_other_user_message)
            textViewTimeIngoing = view.findViewById(R.id.chat_other_user_message_time)
            layoutMessageOutgoing = view.findViewById(R.id.chat_my_layout)
            textViewTextMessageOutgoing = view.findViewById(R.id.chat_my_message)
            textViewTimeOutgoing = view.findViewById(R.id.chat_my_message_time)
        }
    }
}
