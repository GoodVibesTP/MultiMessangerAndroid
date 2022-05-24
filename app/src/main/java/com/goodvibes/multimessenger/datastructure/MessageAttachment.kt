package com.goodvibes.multimessenger.datastructure

import com.goodvibes.multimessenger.network.tgmessenger.Telegram

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

    class TelegramImage(
        val image: Telegram.Image,
        val height: Int,
        val width: Int
    ) : MessageAttachment()
}
