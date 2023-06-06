package com.example.monac.data.category

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionCategory::class], version = 3)
abstract class CategoryDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}