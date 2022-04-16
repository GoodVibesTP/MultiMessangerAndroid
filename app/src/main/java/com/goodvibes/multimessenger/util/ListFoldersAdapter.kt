package com.goodvibes.multimessenger.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Folder
import com.goodvibes.multimessenger.datastructure.Messengers

class ListFoldersAdapter: ArrayAdapter<Folder> {

    public constructor(ctx: Context, chats: MutableList<Folder>) :
            super(ctx, R.layout.list_item_folders, chats){
            }

    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView_ = convertView;
        if (convertView_ == null) {
            convertView_ = LayoutInflater.from(context).inflate(R.layout.list_item_folders, parent,false);
        }

        val folder = getItem(position);
        if (folder!!.folderId == -1){
            val imageView: ImageView = convertView_!!.findViewById(R.id.folders_all_image);
            imageView.setImageResource(R.drawable.ic_baseline_add_24)
        }

        val textViewTitle: TextView = convertView_!!.findViewById(R.id.folders_all_title);
        textViewTitle.setText(folder!!.name);
        return convertView_;
    }
}