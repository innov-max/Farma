package com.example.mkulifarm.data.BasicDataHandling

data class LEDStatus(
    val ledState: Boolean = false, // LED state (on or off)
    val deviceID: String = "",     // Device ID that triggered the action
    val timestamp: String = ""     // Timestamp when the action was triggered
)
