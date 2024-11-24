package com.example.mkulifarm.data.MetricsAnalysis

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mkulifarm.data.BasicDataHandling.MetricData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MetricsViewModel : ViewModel() {
    private val _metrics = MutableStateFlow<List<MetricData>>(emptyList())
    val metrics: StateFlow<List<MetricData>> get() = _metrics

    init {
        fetchWeeklyData()
    }

    private fun fetchWeeklyData() {
        val database = FirebaseDatabase.getInstance()
        val sensorLogsRef = database.getReference("sensorLogs")

        // Calculate timestamp for 7 days ago
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000

        sensorLogsRef.orderByChild("timestamp")
            .startAt(sevenDaysAgo.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val metrics = mutableListOf<MetricData>()

                    for (data in snapshot.children) {
                        val humidity = data.child("humidity").getValue(Double::class.java)?.toFloat() ?: 0f
                        val timestamp = data.child("timestamp").getValue(Long::class.java) ?: 0L

                        metrics.add(MetricData(name = "Humidity", value = humidity, unit = "%", timestamp = timestamp.toString()))
                    }

                    _metrics.value = metrics
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MetricsViewModel", "Failed to fetch data: ${error.message}")
                }
            })
    }
}

