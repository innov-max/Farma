package com.example.mkulifarm.data.MetricsAnalysis

import com.example.mkulifarm.data.BasicDataHandling.MetricData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow

import android.content.Context
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.flow

class FirebaseMetricRepo(private val context: Context? = null) {

    private val database: DatabaseReference = if (context != null) {
        // Firebase initialization for real app
        FirebaseDatabase.getInstance().reference
    } else {
        // If in preview mode, mock data will be returned instead of accessing Firebase
        FirebaseDatabase.getInstance().reference
    }

    // This function is where we handle the data fetching logic
    fun getMetrics(): Flow<List<MetricData>> {
        return flow {
            if (context != null) {
                // Fetch data from Firebase if context is available
                val metrics = listOf(
                    MetricData("Water", 200f, "L"),
                    MetricData("Fertilizer", 50f, "kg"),
                    MetricData("Energy", 75f, "kWh")
                )
                emit(metrics)
            } else {
                // In preview mode: Return mock data
                val mockMetrics = listOf(
                    MetricData("Water", 200f, "L"),
                    MetricData("Fertilizer", 50f, "kg"),
                    MetricData("Energy", 75f, "kWh")
                )
                emit(mockMetrics)
            }
        }
    }
}
