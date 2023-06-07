package com.example.monac.data.transaction

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userID=:userID AND cardID=:cardID ORDER BY (date || time) DESC")
    fun getAllTransactionsForUserAtCard(userID: Long, cardID: Long): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PaymentTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: PaymentTransaction): Int

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}