package com.goodvibes.multimessenger.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Folder
import com.goodvibes.multimessenger.util.ListFoldersAdapter

class SelectFolder(
    _folders: MutableList<Folder>, _listFoldersAdapter: ListFoldersAdapter, chat: Chat,
    _callbackAfterClickFolder: (chat: Chat) ->Unit
) : DialogFragment() {
    private val listFoldersAdapter  = _listFoldersAdapter
    val folders = _folders
    private val callbackAfterClickFolder = _callbackAfterClickFolder
    private val currentChatSelected = chat

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val rowList = layoutInflater.inflate(R.layout.list_view_folders_dialog, null)
            val listView = rowList.findViewById<ListView>(R.id.listView_folders)

            listView.adapter = listFoldersAdapter
            listView.choiceMode = ListView.CHOICE_MODE_SINGLE
            listFoldersAdapter.notifyDataSetChanged()

            listView.setOnItemClickListener { adapterView, view, position, id->
                val folder = listFoldersAdapter.getItem(position)
                currentChatSelected.folder = folder!!
                callbackAfterClickFolder(currentChatSelected)
            }

            builder.setView(rowList)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}