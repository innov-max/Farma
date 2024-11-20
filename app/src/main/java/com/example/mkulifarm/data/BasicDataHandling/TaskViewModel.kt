package com.example.mkulifarm.data.BasicDataHandling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mkulifarm.data.room_backup.AppDatabase
import com.example.mkulifarm.data.room_backup.FakeTaskDao
import com.example.mkulifarm.data.room_backup.Task
import com.example.mkulifarm.data.room_backup.TaskDao
import com.example.mkulifarm.data.room_backup.pushTaskToFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val taskDao: TaskDao = AppDatabase.getDatabase(application).taskDao()  // Default to real TaskDao
) : AndroidViewModel(application) {

    val tasks: LiveData<List<Task>> = taskDao.getAllTasks()

    fun updateTaskCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTask(task.copy(isCompleted = isCompleted))
            pushTaskToFirebase(task.copy(isCompleted = isCompleted))
        }
    }
}




