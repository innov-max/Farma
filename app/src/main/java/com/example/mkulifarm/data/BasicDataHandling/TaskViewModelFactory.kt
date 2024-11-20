package com.example.mkulifarm.data.BasicDataHandling

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mkulifarm.data.room_backup.FakeTaskDao

class TaskViewModelFactory(private val application: Application, private val fakeTaskDao: FakeTaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(application, fakeTaskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
