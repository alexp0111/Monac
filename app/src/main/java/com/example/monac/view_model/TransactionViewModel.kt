package com.example.monac.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.data.transaction.TransactionRepository
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
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val transactionChannel = Channel<Pair<Long, Long>>()
    private val transactionChannelFlow = transactionChannel.receiveAsFlow()

    val allTransactionForUserAtCard = transactionChannelFlow.flatMapLatest { userAndCard ->
        repository.getAllTransactionsForUserAtCard(userAndCard.first, userAndCard.second)
    }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.Lazily, null)

    fun getAllTransactionForUserAtCard(userID: Long, cardID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionChannel.send(Pair(userID, cardID))
        }
    }

    fun updateTransaction(transaction: PaymentTransaction, isSuccess: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isSuccess.invoke(repository.insertTransaction(transaction))
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTransactions()
        }
    }
}