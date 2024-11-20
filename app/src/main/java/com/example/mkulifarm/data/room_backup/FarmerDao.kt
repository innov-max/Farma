package com.example.mkulifarm.data.room_backup

import android.content.Context
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

interface FarmerDao {
    @Insert
    suspend fun insertFarmer(farmer: Farmer)

    @Query("SELECT * FROM farmer_table")
    suspend fun getAllFarmers(): List<Farmer>
}
