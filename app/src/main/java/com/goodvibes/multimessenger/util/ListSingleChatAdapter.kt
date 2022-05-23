package com.goodvibes.multimessenger.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Message


class ListSingleChatAdapter(
    private val ctx: Context,
    val messages: MutableList<Message>,
    val checkedItems: MutableSet<Long>,
    val onItemCheckStateChanged: (MutableSet<Long>) -> Unit
): RecyclerView.Adapter<ListSingleChatAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(ctx)

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

        holder.layoutAttachmentsIngoing.removeAllViews()
        holder.layoutAttachmentsOutgoing.removeAllViews()
        lateinit var layoutAttachments: LinearLayout
        if (message.isMyMessage) {
            holder.layoutMessageOutgoing.visibility = View.VISIBLE
            holder.textViewTextMessageOutgoing.text = message.text
            holder.textViewTimeOutgoing.text = message.time
            holder.unreadMarkerOutgoing.visibility = if (message.read) View.GONE else View.VISIBLE
            holder.layoutMessageIngoing.visibility = View.GONE
            holder.unreadMarkerIngoing.visibility = View.GONE

            layoutAttachments = holder.layoutAttachmentsOutgoing
        }
        else {
            holder.layoutMessageIngoing.visibility = View.VISIBLE
            holder.textViewSenderIngoing.text = "Hardcoded name"
            holder.textViewTextMessageIngoing.text = message.text
            holder.textViewTimeIngoing.text = message.time
            holder.unreadMarkerIngoing.visibility = if (message.read) View.GONE else View.VISIBLE
            holder.layoutMessageOutgoing.visibility = View.GONE
            holder.unreadMarkerOutgoing.visibility = View.GONE

            layoutAttachments = holder.layoutAttachmentsIngoing
        }

        if(checkedItems.contains(messages[position].id)) {
            holder.view.background =
                AppCompatResources.getDrawable(ctx, R.color.list_item_message_checked)
        }
        else {
            holder.view.background =
                AppCompatResources.getDrawable(ctx, R.color.list_item_message_unchecked)
        }

        if (position % 5 == 1) {
            val imageView = ImageView(ctx)
            imageView.setImageResource(R.drawable.kotik)
            imageView.layoutParams = LayoutParams(layoutAttachments.width, layoutAttachments.width)

            layoutAttachments.addView(imageView)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view) {
        var itemChecked: Boolean = false

        internal val layoutMessageIngoing: ConstraintLayout
        internal val textViewSenderIngoing: TextView
        internal val textViewTextMessageIngoing: TextView
        internal val textViewTimeIngoing: TextView
        internal val unreadMarkerIngoing: ImageView
        internal val layoutAttachmentsIngoing: LinearLayout

        internal val layoutMessageOutgoing: ConstraintLayout
        internal val textViewTextMessageOutgoing: TextView
        internal val textViewTimeOutgoing: TextView
        internal val unreadMarkerOutgoing: ImageView
        internal val layoutAttachmentsOutgoing: LinearLayout

        init {
            layoutMessageIngoing = view.findViewById(R.id.chat_other_user_layout)
            textViewTextMessageIngoing = view.findViewById(R.id.chat_other_user_message)
            textViewTimeIngoing = view.findViewById(R.id.chat_other_user_message_time)
            unreadMarkerIngoing = view.findViewById(R.id.chat_other_user_message_unread_marker)
            textViewSenderIngoing = view.findViewById(R.id.chat_other_user_message_sender)
            layoutAttachmentsIngoing = view.findViewById(R.id.chat_other_user_message_layout_attachments)

            layoutMessageOutgoing = view.findViewById(R.id.chat_my_layout)
            textViewTextMessageOutgoing = view.findViewById(R.id.chat_my_message)
            textViewTimeOutgoing = view.findViewById(R.id.chat_my_message_time)
            unreadMarkerOutgoing = view.findViewById(R.id.chat_my_message_unread_marker)
            layoutAttachmentsOutgoing = view.findViewById(R.id.chat_my_message_layout_attachments)

            val listener = View.OnClickListener {
                val itemPosition = absoluteAdapterPosition
                val itemId = messages[itemPosition].id

                itemChecked = if (!itemChecked) {
                    checkedItems.add(itemId)
                    true
                } else {
                    checkedItems.remove(itemId)
                    false
                }
                onItemCheckStateChanged(checkedItems)

                notifyItemChanged(itemPosition)
            }

            layoutMessageIngoing.setOnClickListener(listener)
            layoutMessageOutgoing.setOnClickListener(listener)
        }
    }
}
