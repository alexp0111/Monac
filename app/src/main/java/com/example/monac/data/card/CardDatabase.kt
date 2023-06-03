package com.example.monac.data.card

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.monac.data.user.User
import com.example.monac.data.user.UserDao

@Database(entities = [Card::class], version = 1)
abstract class CardDatabase: RoomDatabase() {
    abstract fun cardDao(): CardDao
}