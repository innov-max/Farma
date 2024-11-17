package com.example.mkulifarm.data.MetricsAnalysis

import com.example.mkulifarm.data.MetricData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockMetricsViewModel(mockData: List<MetricData>) : MetricsViewModel() {
    override val metrics: StateFlow<List<MetricData>> = MutableStateFlow(
        listOf(
            MetricData("Water", 200f, "L"),
            MetricData("Fertilizer", 50f, "kg"),
            MetricData("Energy", 75f, "kWh")
        )
    )
}
