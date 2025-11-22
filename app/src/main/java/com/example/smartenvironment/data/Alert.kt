package com.example.smartenvironment.data

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class AlertType(val color: Color) {
    SUCCESS(Color(0xFF4CAF50)),
    WARNING(Color(0xFFFFC107)),
    ERROR(Color(0xFFF44336))
}

data class AlertData(
    val id: String = "",
    val message: String = "",
    val type: AlertType = AlertType.SUCCESS,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    // Constructor sin argumentos para la deserialización de Firestore
    constructor() : this("", "", AlertType.SUCCESS, null)
}
