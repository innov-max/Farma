package com.example.mkulifarm.data.room_backup

import androidx.room.PrimaryKey

data class Farmer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val farmName: String,
    val farmArea: String,
    val latitude: Double,
    val longitude: Double
)
