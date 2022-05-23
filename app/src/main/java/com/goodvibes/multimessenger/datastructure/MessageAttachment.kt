package com.goodvibes.multimessenger.datastructure

sealed class MessageAttachment {
    class AudioMessage(
        val duration: Int,
        val linkMp3: String,
        val transcription: String
    ) : MessageAttachment()

    class Image(
        val imgUri: String,
        val height: Int,
        val width: Int
    ) : MessageAttachment()
}
