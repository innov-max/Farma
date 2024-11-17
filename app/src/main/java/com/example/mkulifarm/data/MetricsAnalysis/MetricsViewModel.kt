package com.example.mkulifarm.data.MetricsAnalysis

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkulifarm.data.MetricData
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

open class MetricsViewModel(private val context: Context? = null) : ViewModel() {
    private val repository = FirebaseMetricRepo(context)

    open val metrics: StateFlow<List<MetricData>> = repository.getMetrics()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        if (!isPreview()) {
            initializeFirebase()
        }
    }

    private fun isPreview(): Boolean {
        return java.lang.Boolean.getBoolean("isPreview")
    }

    private fun initializeFirebase() {
        // Only initialize Firebase if context is available (not in preview)
        if (context != null && FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }
}
