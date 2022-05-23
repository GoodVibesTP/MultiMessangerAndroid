package com.goodvibes.multimessenger.network.vkmessenger

internal object VK_UPDATE {
    object EVENTS {
        const val MESSAGE_FLAGS_CHANGED = 1
        const val MESSAGE_FLAGS_STATED = 2
        const val NEW_MESSAGE = 4
        const val MESSAGE_EDITED = 5
        const val READ_INGOING = 6
        const val READ_OUTGOING = 7
        const val FRIEND_ONLINE = 8
        const val FRIEND_OFFLINE = 9
        const val DIALOG_INFO_EDITED = 51
        const val CHAT_INFO_EDITED = 52
        const val TYPING_IN_DIALOG = 61
        const val TYPING_IN_CHAT_ONE_USER = 62
        const val TYPING_IN_CHAT_SOME_USERS = 63
    }

    object MESSAGE_FLAGS_STATED {
        const val MESSAGE_ID = 1
        const val FLAGS = 2
        const val MINOR_ID = 3
    }

    object NEW_MESSAGE {
        const val MESSAGE_ID = 1
        const val FLAGS = 2
        const val MINOR_ID = 3
        const val TEXT = 5
        const val ADDITIONAL_FIELD = 7
    }

    object MESSAGE_EDITED {
        const val MESSAGE_ID = 1
        const val FLAGS = 2
        const val MINOR_ID = 3
        const val TEXT = 5
        const val ADDITIONAL_FIELD = 7
    }

    object READ_INGOING {
        const val PEER_ID = 1
        const val MESSAGE_ID = 2
    }

    object READ_OUTGOING {
        const val PEER_ID = 1
        const val MESSAGE_ID = 2
    }
}