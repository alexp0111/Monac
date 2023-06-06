package com.example.monac.data.category

import androidx.room.*
import com.example.monac.util.PaymentType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE userID=:userID")
    fun getAllCategoriesForUser(userID: Long): Flow<List<TransactionCategory>>

    @Query("SELECT * FROM categories WHERE userID=:userID AND type=:type")
    fun getAllTransactionUsersForUser(userID: Long, type: PaymentType): Flow<List<TransactionCategory>>

    @Query("SELECT * FROM categories WHERE id=:id ")
    fun getCategory(id: Long): Flow<TransactionCategory>

    @Query("SELECT * FROM categories WHERE userID=:userID AND name LIKE '%' || :name || '%'")
    fun guessCategoryForUser(userID: Long, name: String): Flow<List<TransactionCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TransactionCategory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserList(list: List<TransactionCategory>): List<Long>

    @Delete
    suspend fun deleteCategory(category: TransactionCategory): Int

    @Query("DELETE FROM categories WHERE userID=:userID")
    suspend fun deleteAllCategoriesForUser(userID: Long)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}