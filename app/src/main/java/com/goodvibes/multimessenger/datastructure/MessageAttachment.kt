package com.goodvibes.multimessenger.datastructure

sealed class MessageAttachment {
    class AudioMessage(
        val duration: Int,
        val link_mp3: String,
        val transcription: String
    ) : MessageAttachment()

    class Image(
        val img_uri: String
    ) : MessageAttachment()
}
