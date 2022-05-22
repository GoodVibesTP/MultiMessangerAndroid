package com.goodvibes.multimessenger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

import kotlinx.coroutines.CoroutineScope

//class ListChatsAdapter(ctx: Context,
//                       val chats: MutableList<Chat>, mainActivity: MainActivity,
//                       diffCallback: DiffUtil.ItemCallback<Chat>
//): PagingDataAdapter<Chat, RecyclerView.ViewHolder>(REPO_COMPARATOR) {
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        (holder as? ViewHolder)?.bind(item = getItem(position))
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return geti
//    }
//
//    private val inflater = LayoutInflater.from(ctx)
//    private var mainActivity : MainActivity = mainActivity
//
//
//    override fun onBindViewHolder(
//        holder: ListChatsAdapter.ViewHolder,
//        position: Int
//    ) {
//        val chat = chats[position];
//        if (chat.messenger == Messengers.VK) {
//            holder.imageViewMessenger.setImageResource(R.drawable.vk)
//        } else {
//            holder.imageViewMessenger.setImageResource(R.mipmap.tg_icon)
//        }
//
//        holder.imageViewAva.setImageResource(chat.img)
//        holder.textViewTitle.text = chat.title
//        holder.textViewLastMessage.text = chat.lastMessage!!.text
//
//    }
//
//    override fun getItemCount(): Int {
//        return chats.size
//    }
//
//    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
//            //get instance of the DoggoImageViewHolder
//                fun getInstance(parent: ViewGroup): ViewHolder {
//                    val inflater = LayoutInflater.from(parent.context)
//                    val view = inflater.inflate(R.layout.list_item_chats, parent, false)
//                    return ViewHolder(view)
//                }
//
//        internal val imageViewAva : ImageView
//        internal val imageViewMessenger : ImageView
//        internal val textViewTitle : TextView
//        internal val textViewLastMessage : TextView
//        init {
//            imageViewAva = view.findViewById(R.id.chat_all_image)
//            imageViewMessenger = view.findViewById(R.id.chat_all_messenger_img)
//            textViewTitle = view.findViewById(R.id.chat_all_title)
//            textViewLastMessage = view.findViewById(R.id.last_message)
//            view.setOnLongClickListener{ view ->
//                if (mainActivity.mActionMode != null) {
//                    false
//                }
//                view.isSelected = true
//                mainActivity.callback.setClickedView(view.verticalScrollbarPosition)
//                mainActivity.mActionMode = mainActivity.startActionMode(mainActivity.callback)!!
//                true
//            }
//            view.setOnClickListener{view ->
//
//                val intent = Intent(mainActivity, ChatActivity::class.java)
//                val chat = mainActivity.listChatsAdapter.chats[adapterPosition]
//                intent.putExtra("Chat", chat)
//                mainActivity.startActivity(intent)
//            }
//        }
//
//    }
//
//
//    //public constructor(ctx: Context, chats: List<Chat>) :
//    //        super(ctx, R.layout.list_item_chats, chats){}
////
//    //public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//    //    var convertView_ = convertView;
//    //    val chat = getItem(position);
//    //    if (convertView_ == null) {Message
//    //        convertView_ = LayoutInflater.from(context).inflate(R.layout.list_item_chats, parent,false);
//    //    }
////
//    //    val imageViewAva: ImageView = convertView_!!.findViewById(R.id.chat_all_image);
//    //    val imageViewMessenger : ImageView = convertView_!!.findViewById(R.id.chat_all_messenger_img);
//    //    val textViewTitle: TextView = convertView_!!.findViewById(R.id.chat_all_title);
//    //    val textViewLastMessage: TextView = convertView_!!.findViewById(R.id.last_message);
//    //    imageViewAva.setImageResource(chat!!.img);
//    //    if (chat!!.messenger == Messengers.VK) {
//    //        imageViewMessenger.setImageResource(R.drawable.vk);
//    //    } else {
//    //        imageViewMessenger.setImageResource(R.mipmap.tg_icon)
//    //    }
////
//    //    textViewTitle.setText(chat!!.title);
//    //    textViewLastMessage.setText(chat!!.lastMessage!!.text);
////
//    //    return convertView_;
//    //}
////
//}

class ListChatsAdapter(context: Context, var mainActivity: MainActivity) :
    PagingDataAdapter<Chat, ChatsViewHolder>(ArticleDiffItemCallback) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        return ChatsViewHolder(layoutInflater.inflate(R.layout.list_item_chats, parent, false), mainActivity)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}

class ChatsViewHolder(itemView: View, var mainActivity: MainActivity) : RecyclerView.ViewHolder(itemView) {

    fun bind(chat: Chat?, position: Int) {
        if (chat != null) {
            val imageViewAva: ImageView = itemView!!.findViewById(R.id.chat_all_image);
            val imageViewMessenger : ImageView = itemView!!.findViewById(R.id.chat_all_messenger_img);
            val textViewTitle: TextView = itemView!!.findViewById(R.id.chat_all_title);
            val textViewLastMessage: TextView = itemView!!.findViewById(R.id.last_message);
            imageViewAva.setImageResource(chat!!.img);
            if (chat!!.messenger == Messengers.VK) {
                imageViewMessenger.setImageResource(R.drawable.vk);
            } else {
                imageViewMessenger.setImageResource(R.mipmap.tg_icon)
            }
            textViewTitle.setText(chat!!.title);
            textViewLastMessage.setText(chat!!.lastMessage!!.text);

            itemView.setOnClickListener{view ->
                val intent = Intent(mainActivity, ChatActivity::class.java)
                intent.putExtra("Chat", chat)
                mainActivity.startActivity(intent)
            }

            itemView.setOnLongClickListener{ view ->
                if (mainActivity.mActionMode != null) {
                    false
                }
                view.isSelected = true
                mainActivity.callback.setClickedView(view.verticalScrollbarPosition)
                mainActivity.mActionMode = mainActivity.startActionMode(mainActivity.callback)!!
                true
            }

        }
    }
}

private object ArticleDiffItemCallback : DiffUtil.ItemCallback<Chat>() {

    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.title == newItem.title && oldItem.messenger == newItem.messenger
    }
}