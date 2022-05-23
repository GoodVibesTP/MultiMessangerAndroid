package com.goodvibes.multimessenger.datastructure

sealed class Event {
    class DeleteMessage(
        val chat_id: Long,
        val message_id: Long,
        val messenger: Messengers
    ) : Event()

    class EditMessage(
        val message: Message
    ) : Event()

    class EditMessageContent(
        val chat_id: Long,
        val message_id: Long,
        val text: String,
        val messenger: Messengers
    ) : Event()

    class NewMessage(
        val message: Message,
        val direction: Direction
    ) : Event() {
        enum class Direction {
            INGOING,
            OUTGOING
        }
    }

    class ReadIngoingUntil(
        val chat_id: Long,
        val message_id: Long,
        val messenger: Messengers
    ) : Event()

    class ReadOutgoingUntil(
        val chat_id: Long,
        val message_id: Long,
        val messenger: Messengers
    ) : Event()

    class DefaultEvent() : Event()
}
