package com.example.monac.data.category

import com.example.monac.util.UiState
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val db_categories: CategoryDatabase
) {

    private val categoryDao = db_categories.categoryDao()

    // functions

    fun getAllCategoriesForUser(userID: Long) = channelFlow<UiState<List<TransactionCategory>>> {
        categoryDao.getAllCategoriesForUser(userID).collect { send(UiState.Success(it)) }
    }

    fun guessCategoryForUser(userID: Long, name: String) =
        channelFlow<UiState<List<TransactionCategory>>> {
            categoryDao.guessCategoryForUser(userID, name).collect { send(UiState.Success(it)) }
        }

    suspend fun insertCategory(category: TransactionCategory) =
        categoryDao.insertCategory(category) != -1L

    suspend fun deleteAllCategoriesForUser(userID: Long) =
        categoryDao.deleteAllCategoriesForUser(userID)

    suspend fun deleteAllCategories() = categoryDao.deleteAllCategories()
}