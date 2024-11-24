package com.example.mkulifarm.data.map

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FarmAreaDao {
    @Insert
    suspend fun insertFarmArea(farmArea: FarmAreaEntity)

    @Query("SELECT * FROM farm_areas")
    suspend fun getAllFarmAreas(): List<FarmAreaEntity>
}
