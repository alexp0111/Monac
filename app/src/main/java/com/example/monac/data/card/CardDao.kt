package com.example.monac.data.card

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE userID=:userID")
    fun getAllCardsForUser(userID: Long): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id=:id")
    fun getCard(id: Long): Flow<Card>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Delete
    suspend fun deleteCard(card: Card): Int

    @Query("DELETE FROM cards WHERE userID=:userID")
    suspend fun deleteAllCardsForUser(userID: Long)

    @Query("DELETE FROM cards")
    suspend fun deleteAllCards()
}