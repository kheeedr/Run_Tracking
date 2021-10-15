package com.khedr.runtracking.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.khedr.runtracking.data.local.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity)

    @Delete
    suspend fun deleteRun(run: RunEntity)

    @Query("select * from runs_table order by :columnName desc")
    fun getAllRunsSortedBy(columnName: String): Flow<RunEntity>

    @Query("SELECT SUM(:columnName) FROM runs_table")
    fun getTotalOf(columnName: String): Flow<Int>

    @Query("SELECT AVG(:columnName) FROM runs_table")
    fun getAvgOf(columnName: String): Flow<Float>


}