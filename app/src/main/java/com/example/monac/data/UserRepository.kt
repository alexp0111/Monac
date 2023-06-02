package com.example.monac.data

import com.example.monac.data.user.User
import com.example.monac.data.user.UserDatabase
import com.example.monac.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db_users: UserDatabase
) {

    private val userDao = db_users.userDao()

    // functions

    fun getAllUsers() = channelFlow<UiState<List<User>>> {
        userDao.getAllUsers().collect { send(UiState.Success(it)) }
    }

    fun getUser(id: Long) = channelFlow<UiState<User>> {
        userDao.getUser(id).collect { send(UiState.Success(it)) }
    }

    suspend fun insertUser(user: User) = userDao.insertUser(user) != -1L

    suspend fun deleteAllUsers() = userDao.deleteAllUsers()
}