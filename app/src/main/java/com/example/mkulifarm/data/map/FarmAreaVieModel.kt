package com.example.mkulifarm.data.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FarmAreaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FarmAreaRepository

    init {
        val database = FarmAreaDatabase.getDatabase(application)
        repository = FarmAreaRepository(database.farmAreaDao())
    }

    fun insertFarmArea(farmArea: FarmAreaEntity) {
        viewModelScope.launch {
            repository.insertFarmArea(farmArea)
        }
    }

    fun getAllFarmAreas(callback: (List<FarmAreaEntity>) -> Unit) {
        viewModelScope.launch {
            val areas = repository.getAllFarmAreas()
            callback(areas)
        }
    }
}
