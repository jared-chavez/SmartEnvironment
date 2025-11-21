package com.example.smartenvironment.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Modelo de datos para un recordatorio en el pizarrón familiar.
 *
 * @property id El ID único del documento de Firestore.
 * @property text El contenido del mensaje del recordatorio.
 * @property createdAt La fecha y hora en que se creó el recordatorio. Se asigna automáticamente por el servidor.
 * @property reminderAt La fecha y hora programada para el recordatorio (opcional).
 * @property author El nombre de quien creó el recordatorio (actualmente no se usa, pero es útil para el futuro).
 * @property isCompleted Indica si el recordatorio ha sido completado.
 */
data class Reminder(
    val id: String = "",
    val text: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val reminderAt: Timestamp? = null,
    val author: String = "Familia",
    val isCompleted: Boolean = false
)
