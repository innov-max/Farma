package com.example.mkulifarm.data.map

class FarmAreaRepository(private val farmAreaDao: FarmAreaDao) {

    suspend fun insertFarmArea(farmArea: FarmAreaEntity) {
        farmAreaDao.insertFarmArea(farmArea)
    }

    suspend fun getAllFarmAreas(): List<FarmAreaEntity> {
        return farmAreaDao.getAllFarmAreas()
    }
}
