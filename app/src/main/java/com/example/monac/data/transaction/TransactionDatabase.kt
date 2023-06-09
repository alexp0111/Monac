package com.example.monac.data.transaction

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PaymentTransaction::class], version = 3)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}