package com.khedr.runtracking.di

import com.khedr.runtracking.data.local.RunDao
import com.khedr.runtracking.data.repository.RunRepository
import com.khedr.runtracking.domain.repository.RunRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepositoryImpl(runDao: RunDao): RunRepository {
        return RunRepositoryImpl(runDao)
    }
}