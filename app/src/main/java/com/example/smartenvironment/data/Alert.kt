package com.example.smartenvironment.data

import androidx.compose.ui.graphics.Color

enum class AlertType(val color: Color) {
    SUCCESS(Color(0xFF4CAF50)),
    WARNING(Color(0xFFFFC107)),
    ERROR(Color(0xFFF44336))
}

data class AlertData(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: AlertType
)
