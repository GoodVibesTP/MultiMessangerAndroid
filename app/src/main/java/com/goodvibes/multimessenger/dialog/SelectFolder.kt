package com.goodvibes.multimessenger.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Folder
import com.goodvibes.multimessenger.datastructure.idNewFolder
import com.goodvibes.multimessenger.util.ListFoldersAdapter

class SelectFolder(
    _folders: MutableList<Folder>, _listFoldersAdapter: ListFoldersAdapter, chat: Chat,
    _callbackAfterClickFolder: (chat: Chat) ->Unit, _callbackAddFolder: (chat: Chat)->Unit
) : DialogFragment() {
    private val listFoldersAdapter  = _listFoldersAdapter
    val folders = _folders
    private val callbackAfterClickFolder = _callbackAfterClickFolder
    private val callbackAddFolder = _callbackAddFolder
    private val currentChatSelected = chat

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val rowList = layoutInflater.inflate(R.layout.list_view_folders_dialog, null)
            val listView = rowList.findViewById<ListView>(R.id.listView_folders)

            val addNewFolderView = rowList.findViewById<RelativeLayout>(R.id.folders_all_add_relative_layout)
            addNewFolderView.setOnClickListener(View.OnClickListener { view ->
                Log.d("FOLDER DIALOG", "click on create new folder")

                val builderAddFolderDialog = AlertDialog.Builder(it)
                val layoutAddNewFolderDialog = layoutInflater.inflate(R.layout.add_new_folder_dialog, null)
                builderAddFolderDialog.setView(layoutAddNewFolderDialog)

                val userInput = layoutAddNewFolderDialog.findViewById<EditText>(R.id.input_text_add_new_folder)
                builderAddFolderDialog.setCancelable(true)
                builderAddFolderDialog.setPositiveButton("OK") { dialog, which ->
                    val folderInput = userInput.findViewById(R.id.input_text_add_new_folder) as EditText
                    val nameNewFolder = folderInput.text.toString()
                    currentChatSelected.folder.name = nameNewFolder
                    currentChatSelected.folder.folderId = idNewFolder
                    callbackAddFolder(currentChatSelected)
                }

                builderAddFolderDialog.show()
            })

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