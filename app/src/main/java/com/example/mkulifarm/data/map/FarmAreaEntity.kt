package com.example.mkulifarm.data.map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_areas")
data class FarmAreaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val area: Double,
    val coordinates: String // Store as a JSON string for simplicity
)

