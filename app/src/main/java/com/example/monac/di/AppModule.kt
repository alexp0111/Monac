package com.example.monac.di

import android.app.Application
import androidx.room.Room
import com.example.monac.data.card.CardDatabase
import com.example.monac.data.transaction.TransactionDatabase
import com.example.monac.data.user.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): UserDatabase =
        Room.databaseBuilder(app, UserDatabase::class.java, "user_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCardDatabase(app: Application): CardDatabase =
        Room.databaseBuilder(app, CardDatabase::class.java, "card_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideTransactionDatabase(app: Application): TransactionDatabase =
        Room.databaseBuilder(app, TransactionDatabase::class.java, "transactions_database")
            .fallbackToDestructiveMigration()
            .build()
}