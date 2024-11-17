package com.example.mkulifarm.data.MetricsAnalysis

import android.content.Context
import com.google.firebase.FirebaseApp
import com.example.mkulifarm.data.MetricData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

open class MetricsViewModel(private val context: Context? = null) : ViewModel() {

    // Firebase repository to fetch metric data
    private val repository = FirebaseMetricRepo(context)

    // StateFlow to hold the list of MetricData and notify the UI when data changes
    open val metrics: StateFlow<List<MetricData>> = repository.getMetrics()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Initialize Firebase or load real data, but only if not in preview mode
        if (!isPreview()) {
            initializeFirebase()
        }
    }

    // A simple helper function to check if the code is running in preview mode
    private fun isPreview(): Boolean {
        return java.lang.Boolean.getBoolean("isPreview")
    }

    // Function to initialize Firebase
    private fun initializeFirebase() {
        // Only initialize Firebase if context is provided and not in preview mode
        if (context != null && FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }
}
