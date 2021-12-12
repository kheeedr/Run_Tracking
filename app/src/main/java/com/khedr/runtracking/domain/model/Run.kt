package com.khedr.runtracking.domain.model

import android.graphics.Bitmap
import com.khedr.runtracking.data.local.entity.RunEntity

class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0,
)

fun Run.toRunEntity(): RunEntity {
    return RunEntity(
        img = img,
        timestamp = timestamp,
        avgSpeedInKMH = avgSpeedInKMH,
        distanceInMeters = distanceInMeters,
        timeInMillis = timeInMillis,
        caloriesBurned = caloriesBurned
    )

}