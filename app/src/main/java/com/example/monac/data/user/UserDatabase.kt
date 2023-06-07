package com.example.monac.data.user

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class], version = 2)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}