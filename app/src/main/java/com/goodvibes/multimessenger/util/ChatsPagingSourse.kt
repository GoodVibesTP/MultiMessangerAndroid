package com.goodvibes.multimessenger.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.Messenger
import com.goodvibes.multimessenger.usecase.MainActivityUC
import okhttp3.internal.notifyAll
import okhttp3.internal.wait
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.goodvibes.multimessenger.network.vkmessenger.VK
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import okhttp3.internal.notify

class ChatsPagingSourse(
    private val useCase: MainActivityUC
) : PagingSource<Int, Chat>() {
    private var messenger : Messengers = Messengers.VK
    private var totalLoadVK : Int = 0
    private var totalLoadTG : Int = 0

    override fun getRefreshKey(state: PagingState<Int, Chat>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Chat> {
        val page: Int = params.key ?: 0
        val pageSize: Int = params.loadSize
        val chats: MutableList<Chat>
        var transfer: Boolean = true
        return suspendCoroutine { continuation ->
            val page: Int = params.key ?: 0
            val pageSize: Int = params.loadSize

            var nextPageNumber: Int? = 0
            var prevPageNumber: Int? = 0

            if (messenger == Messengers.VK) {
                if (useCase.isLogin(VK)) {
                    useCase.getAllChats(pageSize, totalLoadVK, VK, { chats ->
                        totalLoadVK += chats.size
                        nextPageNumber = if (chats.isEmpty()) null else page + 1
                        prevPageNumber = if (page > 1) page - 1 else null
                        continuation.resume(LoadResult.Page(chats, prevPageNumber, nextPageNumber))
                    })
                }
                messenger = Messengers.TELEGRAM
            } else {
                useCase.getAllChats(pageSize, totalLoadTG, Telegram, { chats ->
                    totalLoadTG += chats.size
                    nextPageNumber = if (chats.isEmpty()) null else page + 1
                    prevPageNumber = if (page > 1) page - 1 else null
                    continuation.resume(LoadResult.Page(chats, prevPageNumber, nextPageNumber))
                })
                messenger = Messengers.VK
            }
        }
    }
}