package com.khedr.runtracking.data.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khedr.runtracking.data.local.entity.RunEntity
import kotlinx.coroutines.flow.Flow

interface RunRepository {

    suspend fun insertRun(run: RunEntity)

    suspend fun deleteRun(run: RunEntity)

    fun getAllRunsSortedBy(columnName: String): Flow<RunEntity>

    fun getTotalOf(columnName: String): Flow<Int>

    fun getAvgOf(columnName: String): Flow<Float>
}