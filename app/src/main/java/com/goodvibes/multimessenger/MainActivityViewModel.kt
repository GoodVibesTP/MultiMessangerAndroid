package com.goodvibes.multimessenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.usecase.MainActivityUC
import com.goodvibes.multimessenger.util.ChatsPagingSourse
import kotlinx.coroutines.flow.Flow

class MainActivityViewModel(): ViewModel() {
    lateinit var useCase: MainActivityUC
    fun getListData(): Flow<PagingData<Chat>> {
        return Pager(config = PagingConfig(pageSize = 5, initialLoadSize = 5),
            pagingSourceFactory = {
                ChatsPagingSourse(this.useCase)
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun setUC(useCase:MainActivityUC) {
        this.useCase = useCase
    }
}
