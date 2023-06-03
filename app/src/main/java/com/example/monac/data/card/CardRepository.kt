package com.example.monac.data.card

import com.example.monac.util.UiState
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val db_cards: CardDatabase
) {

    private val cardDao = db_cards.cardDao()

    // functions

    fun getAllCardsForUser(userID: Long) = channelFlow<UiState<List<Card>>> {
        cardDao.getAllCardsForUser(userID).collect { send(UiState.Success(it)) }
    }

    suspend fun insertCard(card: Card) = cardDao.insertCard(card) != -1L

    suspend fun deleteAllCardsForUser(userID: Long) = cardDao.deleteAllCardsForUser(userID)
    suspend fun deleteAllCards() = cardDao.deleteAllCards()
}