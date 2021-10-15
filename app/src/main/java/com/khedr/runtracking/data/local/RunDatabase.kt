package com.khedr.runtracking.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.khedr.runtracking.data.local.entity.RunEntity

@Database(entities = [RunEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao

}