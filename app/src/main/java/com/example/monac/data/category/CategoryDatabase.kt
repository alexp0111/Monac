package com.example.monac.data.category

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.monac.data.card.Card
import com.example.monac.data.card.CardDao

@Database(entities = [TransactionCategory::class], version = 2)
abstract class CategoryDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}