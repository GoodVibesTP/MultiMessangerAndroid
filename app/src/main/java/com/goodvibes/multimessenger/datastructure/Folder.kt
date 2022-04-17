package com.goodvibes.multimessenger.datastructure

const val idNewFolder = -1
const val idTGFolder = -2
const val idVKFolder = -3

data class Folder(
    var folderId: Int,
    var name: String,
    )
