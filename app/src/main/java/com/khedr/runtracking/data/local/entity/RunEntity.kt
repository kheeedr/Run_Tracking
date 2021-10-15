package com.khedr.runtracking.data.local.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.khedr.runtracking.domain.model.Run

@Entity(tableName = "runs_table")
class RunEntity(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

fun RunEntity.toRun(): Run {
    return Run(
        img = img,
        timestamp = timestamp,
        avgSpeedInKMH = avgSpeedInKMH,
        distanceInMeters = distanceInMeters,
        timeInMillis = timeInMillis,
        caloriesBurned = caloriesBurned
    )
}

