package com.khedr.runtracking.di

import android.content.Context
import androidx.room.Room
import com.khedr.runtracking.data.local.RunDao
import com.khedr.runtracking.data.local.RunDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseInstance(@ApplicationContext context: Context): RunDatabase {
        return Room.databaseBuilder(
            context,
            RunDatabase::class.java,
            "run_database"
        )
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun providedDao(database: RunDatabase): RunDao {
        return database.runDao()
    }
}