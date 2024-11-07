package com.example.mkulifarm.data.room_backup

import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase

interface FarmerDao {
    @Insert
    suspend fun insertFarmer(farmer: Farmer)

    @Query("SELECT * FROM farmer_table")
    suspend fun getAllFarmers(): List<Farmer>
}

@Database(entities = [Farmer::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmerDao(): FarmerDao
}