package com.example.monac.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monac.data.card.Card
import com.example.monac.data.card.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    private val cardChannel = Channel<Long>()
    private val cardChannelFlow = cardChannel.receiveAsFlow()

    val allCardsForUser = cardChannelFlow.flatMapLatest { userID ->
        repository.getAllCardsForUser(userID)
    }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.Lazily, null)

    fun getAllCardsForUser(userID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            cardChannel.send(userID)
        }
    }

    fun updateCard(card: Card, isSuccess: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isSuccess.invoke(repository.insertCard(card))
        }
    }

    fun deleteAllCardsForUser(userID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCardsForUser(userID)
        }
    }

}