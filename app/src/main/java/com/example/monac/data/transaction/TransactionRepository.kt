package com.example.monac.data.transaction

import com.example.monac.util.UiState
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val db_transactions: TransactionDatabase
) {
    private val transactionsDao = db_transactions.transactionDao()

    // functions

    fun getAllTransactionsForUserAtCard(userID: Long, cardID: Long) =
        channelFlow<UiState<List<PaymentTransaction>>> {
            transactionsDao.getAllTransactionsForUserAtCard(userID, cardID)
                .collect { send(UiState.Success(it)) }
        }

    suspend fun insertTransaction(transaction: PaymentTransaction) =
        transactionsDao.insertTransaction(transaction) != -1L

    suspend fun deleteAllCardsForUser(transaction: PaymentTransaction) =
        transactionsDao.deleteTransaction(transaction)

    suspend fun deleteAllCards() = transactionsDao.deleteAllTransactions()
}