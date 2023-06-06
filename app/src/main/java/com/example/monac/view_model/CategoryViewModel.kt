package com.example.monac.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monac.data.category.CategoryRepository
import com.example.monac.data.category.TransactionCategory
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
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {
    private val categoryChannel = Channel<Long>()
    private val categoryChannelFlow = categoryChannel.receiveAsFlow()

    private val categoryUserChannel = Channel<Long>()
    private val categoryUserChannelFlow = categoryUserChannel.receiveAsFlow()

    private val categoryGuessChannel = Channel<Pair<Long, String>>()
    private val categoryGuessChannelFlow = categoryGuessChannel.receiveAsFlow()


    val allCategoriesForUser = categoryChannelFlow.flatMapLatest { userID ->
        repository.getAllCategoriesForUser(userID)
    }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.Lazily, null)

    val allTransactionUsersForUser = categoryUserChannelFlow.flatMapLatest { userID ->
        repository.getAllTransactionUsersForUser(userID)
    }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.Lazily, null)

    val guessedCategoriesForUser = categoryGuessChannelFlow.flatMapLatest { userIdAndGuessName ->
        repository.guessCategoryForUser(userIdAndGuessName.first, userIdAndGuessName.second)
    }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.Lazily, null)

    fun getAllCategoriesForUser(userID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryChannel.send(userID)
        }
    }

    fun getAllTransactionUsersForUser(userID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryUserChannel.send(userID)
        }
    }

    fun guessCategoriesForUser(userID: Long, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryGuessChannel.send(Pair(userID, name))
        }
    }

    fun updateCategory(category: TransactionCategory, isSuccess: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isSuccess.invoke(repository.insertCategory(category))
        }
    }

    fun updateUsers(list: List<TransactionCategory>, isSuccess: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isSuccess.invoke(repository.updateUserList(list))
        }
    }

    fun deleteAllCategoriesForUser(userID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCategoriesForUser(userID)
        }
    }
}