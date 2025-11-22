package com.example.smartenvironment.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

// Se eliminó la propiedad de color de la enumeración.
// La lógica del color ahora debe estar en la capa de la interfaz de usuario.
enum class AlertType {
    SUCCESS,
    WARNING,
    ERROR
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
