package com.example.monac.view_model

import androidx.lifecycle.ViewModel
import com.example.monac.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val userChannel = Channel<List<String>>()
    private val userChannelFlow = userChannel.receiveAsFlow()

    fun getAllUsers(){

    }
}