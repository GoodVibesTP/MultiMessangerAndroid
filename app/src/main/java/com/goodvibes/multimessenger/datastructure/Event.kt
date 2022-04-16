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
}
