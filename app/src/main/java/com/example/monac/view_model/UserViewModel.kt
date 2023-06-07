package com.example.monac.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monac.data.user.UserRepository
import com.example.monac.data.user.User
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
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val userChannel = Channel<String>()
    private val userChannelFlow = userChannel.receiveAsFlow()

    val allUsers = userChannelFlow.flatMapLatest {
        repository.getAllUsers()
    }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.Lazily, null)

    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userChannel.send("New request")
        }
    }

    fun updateUser(user: User, id: (Long) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            id.invoke(repository.insertUser(user))
        }
    }

    fun deleteAllUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllUsers()
        }
    }
}