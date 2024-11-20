package com.example.mkulifarm.data.room_backup

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mkulifarm.data.BasicDataHandling.TaskViewModel
class FakeTaskDao : TaskDao {
    private val tasks = mutableListOf(
        Task(id = 1, title = "Irrigate Field", description = "Water the crops at 9 AM", isCompleted = false, time = "9:00 AM"),
        Task(id = 2, title = "Add Fertilizer", description = "Apply fertilizer to wheat field", isCompleted = true, time = "12:00 PM")
    )

    override fun getAllTasks(): LiveData<List<Task>> {
        return MutableLiveData(tasks)
    }

    override suspend fun insertTask(task: Task) {
        tasks.add(task)
    }

    override suspend fun updateTask(task: Task) {
        tasks.replaceAll { if (it.id == task.id) task else it }
    }

    suspend fun deleteTask(task: Task) {
        tasks.remove(task)
    }
}
