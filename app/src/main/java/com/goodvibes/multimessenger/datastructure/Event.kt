package com.goodvibes.multimessenger.datastructure

sealed class Event {
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
}
