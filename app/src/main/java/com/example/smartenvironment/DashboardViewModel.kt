package com.example.smartenvironment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartenvironment.data.AlertData
import com.example.smartenvironment.data.AlertType
import com.example.smartenvironment.data.Reminder
import com.example.smartenvironment.data.WeatherData
import com.example.smartenvironment.data.WeatherIconType
import com.example.smartenvironment.data.remote.WeatherApiService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class DashboardViewModel : ViewModel() {

    // --- Estados de la UI ---
    var lightStatus by mutableStateOf(false)
        private set
    var bluetoothStatus by mutableStateOf(false)
        private set
    var coffeeMakerStatus by mutableStateOf(false)
        private set
    var weatherData by mutableStateOf<WeatherData?>(null)
        private set
    var weatherLocation by mutableStateOf("Saltillo")
        private set
    var weatherStatusMessage by mutableStateOf("Cargando clima...")
        private set
    var familyMessage by mutableStateOf("Cargando mensaje...")
        private set
    var showLocationDialog by mutableStateOf(false)
        private set
    var reminders by mutableStateOf<List<Reminder>>(emptyList())
        private set
    var showAddReminderDialog by mutableStateOf(false)
        private set
    val alerts = mutableStateListOf<AlertData>()

    // --- Referencias a Firebase ---
    private val firestore = Firebase.firestore
    private val lightDocRef = firestore.collection("smarthome_devices").document("living_room_light")
    private val bluetoothDocRef = firestore.collection("smarthome_devices").document("bluetooth_speaker")
    private val coffeeDocRef = firestore.collection("smarthome_devices").document("coffee_maker")
    private val remindersCollectionRef = firestore.collection("reminders")
    private val settingsDocRef = firestore.collection("settings").document("user_settings")

    private val apiService = WeatherApiService.create()

    init {
        listenToWeatherLocation()
        listenToFamilyMessage()
        listenToReminders()
        // Iniciar listeners para todos los dispositivos
        listenToDeviceStatus(lightDocRef, "Luz de la sala") { lightStatus = it }
        listenToDeviceStatus(bluetoothDocRef, "Bocina Bluetooth") { bluetoothStatus = it }
        listenToDeviceStatus(coffeeDocRef, "Cafetera") { coffeeMakerStatus = it }
    }

    // --- Lógica de UI ---
    fun openLocationDialog() { showLocationDialog = true }
    fun closeLocationDialog() { showLocationDialog = false }
    fun openAddReminderDialog() { showAddReminderDialog = true }
    fun closeAddReminderDialog() { showAddReminderDialog = false }
    fun dismissAlert(alertId: Long) {
        alerts.removeAll { it.id == alertId }
    }

    // --- Lógica de Dispositivos (Generalizada) ---
    private fun listenToDeviceStatus(docRef: DocumentReference, deviceName: String, onStateChange: (Boolean) -> Unit) {
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                showAlert("Error al leer $deviceName", AlertType.ERROR)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                onStateChange(snapshot.getBoolean("isOn") ?: false)
            } else {
                docRef.set(mapOf("isOn" to false)) // Crea el documento si no existe
            }
        }
    }

    fun toggleDeviceStatus(docRef: DocumentReference, deviceName: String, currentStatus: Boolean) {
        docRef.update("isOn", !currentStatus)
            .addOnSuccessListener { showAlert("$deviceName ${if (!currentStatus) "encendida" else "apagada"}", AlertType.SUCCESS) }
            .addOnFailureListener { showAlert("Error al cambiar $deviceName", AlertType.ERROR) }
    }

    // --- Lógica específica (opcional, para claridad) ---
    fun toggleLightStatus() = toggleDeviceStatus(lightDocRef, "Luz de la sala", lightStatus)
    fun toggleBluetoothStatus() = toggleDeviceStatus(bluetoothDocRef, "Bocina Bluetooth", bluetoothStatus)
    fun toggleCoffeeMakerStatus() = toggleDeviceStatus(coffeeDocRef, "Cafetera", coffeeMakerStatus)

    // --- Lógica de Datos ---
    private fun listenToFamilyMessage() {
        firestore.collection("board").document("current_message")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    familyMessage = "Error al cargar mensaje"
                    showAlert("Error al leer el pizarrón", AlertType.ERROR)
                    return@addSnapshotListener
                }
                familyMessage = if (snapshot != null && snapshot.exists()) {
                    snapshot.getString("text") ?: "No hay mensaje."
                } else {
                    "Aún no hay mensajes."
                }
            }
    }

    private fun listenToWeatherLocation() {
        settingsDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                fetchWeather("Saltillo") // Usar default en caso de error
                showAlert("Error al leer la ubicación", AlertType.WARNING)
                return@addSnapshotListener
            }
            val locationFromDb = snapshot?.getString("weather_location")
            if (locationFromDb != null && locationFromDb != weatherLocation) {
                weatherLocation = locationFromDb
                fetchWeather(locationFromDb)
            } else if (locationFromDb == null) {
                fetchWeather(weatherLocation) // Cargar clima por primera vez
                settingsDocRef.set(mapOf("weather_location" to weatherLocation))
            }
        }
    }

    fun updateWeatherLocation(newLocation: String) {
        if (newLocation.isNotBlank() && newLocation != weatherLocation) {
            settingsDocRef.update("weather_location", newLocation)
                .addOnSuccessListener { showAlert("Ubicación actualizada a $newLocation", AlertType.SUCCESS) }
                .addOnFailureListener { showAlert("Error al guardar la ubicación", AlertType.ERROR) }
        }
    }

    fun fetchWeather(location: String) {
        viewModelScope.launch {
            weatherData = null
            weatherStatusMessage = "Cargando clima..."
            try {
                val response = apiService.getCurrentWeather(location = location, apiKey = "f32b96c8ed2ce5a044e1e49dd20bf6b3", units = "metric", lang = "es")
                val temp = response.main.temp.toInt()
                val weather = response.weather.firstOrNull()
                val description = weather?.description?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "No disponible"
                val iconType = weather?.let { getWeatherIconType(it.main, it.description, it.icon) } ?: WeatherIconType.UNKNOWN
                weatherData = WeatherData(temp, description, iconType)
            } catch (e: Exception) {
                e.printStackTrace()
                weatherStatusMessage = "Error al cargar el clima"
                showAlert("No se pudo cargar el clima para $location", AlertType.ERROR)
            }
        }
    }

    private fun getWeatherIconType(main: String, description: String, iconCode: String): WeatherIconType {
        return when (main) {
            "Clear" -> if (iconCode.endsWith("d")) WeatherIconType.SUNNY else WeatherIconType.NIGHT
            "Clouds" -> when {
                description.contains("pocas nubes", ignoreCase = true) -> WeatherIconType.PARTLY_CLOUDY
                description.contains("nubes dispersas", ignoreCase = true) -> WeatherIconType.PARTLY_CLOUDY
                else -> WeatherIconType.CLOUDY
            }
            "Rain", "Drizzle", "Thunderstorm" -> WeatherIconType.RAINY
            else -> WeatherIconType.UNKNOWN
        }
    }

    private fun listenToReminders() {
        remindersCollectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    showAlert("Error al cargar recordatorios", AlertType.ERROR)
                    return@addSnapshotListener
                }
                val reminderList = snapshot?.documents?.mapNotNull { it.toObject(Reminder::class.java)?.copy(id = it.id) } ?: emptyList()
                reminders = reminderList.sortedWith(compareBy<Reminder> { it.isCompleted }.thenByDescending { it.createdAt?.seconds })
            }
    }

    fun addReminder(text: String, reminderDate: Date?) {
        if (text.isBlank()) {
            showAlert("El recordatorio no puede estar vacío", AlertType.WARNING)
            return
        }
        val reminder = Reminder(text = text, reminderAt = reminderDate?.let { Timestamp(it) }, isCompleted = false)
        remindersCollectionRef.add(reminder)
            .addOnSuccessListener { showAlert("Recordatorio añadido", AlertType.SUCCESS) }
            .addOnFailureListener { showAlert("Error al añadir recordatorio", AlertType.ERROR) }
    }

    fun toggleReminderCompleted(reminderId: String, isCompleted: Boolean) {
        remindersCollectionRef.document(reminderId).update("completed", isCompleted)
            .addOnSuccessListener { showAlert("Recordatorio ${if (isCompleted) "completado" else "restaurado"}", AlertType.SUCCESS) }
            .addOnFailureListener { showAlert("Error al actualizar recordatorio", AlertType.ERROR) }
    }

    fun deleteReminder(reminderId: String) {
        remindersCollectionRef.document(reminderId).delete()
            .addOnSuccessListener { showAlert("Recordatorio eliminado", AlertType.SUCCESS) }
            .addOnFailureListener { showAlert("Error al eliminar recordatorio", AlertType.ERROR) }
    }

    private fun showAlert(message: String, type: AlertType) {
        alerts.add(AlertData(message = message, type = type))
    }
}
