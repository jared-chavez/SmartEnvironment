package com.example.smartenvironment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartenvironment.data.remote.WeatherApiService
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardViewModel : ViewModel() {

    // --- Estados de la UI ---
    var lightStatus by mutableStateOf(false)
        private set
    var bluetoothStatus by mutableStateOf(false)
        private set
    var coffeeMakerStatus by mutableStateOf(false)
        private set
    var weatherInfo by mutableStateOf("Cargando clima...")
        private set
    var familyMessage by mutableStateOf("Cargando mensaje...")
        private set
    var showLocationDialog by mutableStateOf(false)
        private set

    // --- Referencias a Firebase ---
    private val firestore = Firebase.firestore
    private val lightDocRef = firestore.collection("smarthome_devices").document("living_room_light")
    private val bluetoothDocRef = firestore.collection("smarthome_devices").document("bluetooth_speaker")
    private val coffeeDocRef = firestore.collection("smarthome_devices").document("coffee_maker")

    private val apiService = WeatherApiService.create()

    init {
        fetchWeather()
        listenToFamilyMessage()
        // Iniciar listeners para todos los dispositivos
        listenToDeviceStatus(lightDocRef) { lightStatus = it }
        listenToDeviceStatus(bluetoothDocRef) { bluetoothStatus = it }
        listenToDeviceStatus(coffeeDocRef) { coffeeMakerStatus = it }
    }

    // --- Lógica de UI ---
    fun openLocationDialog() { showLocationDialog = true }
    fun closeLocationDialog() { showLocationDialog = false }

    // --- Lógica de Dispositivos (Generalizada) ---
    private fun listenToDeviceStatus(docRef: DocumentReference, onStateChange: (Boolean) -> Unit) {
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot != null && snapshot.exists()) {
                onStateChange(snapshot.getBoolean("isOn") ?: false)
            } else {
                docRef.set(mapOf("isOn" to false)) // Crea el documento si no existe
            }
        }
    }

    fun toggleDeviceStatus(docRef: DocumentReference, currentStatus: Boolean) {
        docRef.update("isOn", !currentStatus)
    }
    
    // --- Lógica específica (opcional, para claridad) ---
    fun toggleLightStatus() = toggleDeviceStatus(lightDocRef, lightStatus)
    fun toggleBluetoothStatus() = toggleDeviceStatus(bluetoothDocRef, bluetoothStatus)
    fun toggleCoffeeMakerStatus() = toggleDeviceStatus(coffeeDocRef, coffeeMakerStatus)


    // --- Lógica de Datos ---
    private fun listenToFamilyMessage() {
        firestore.collection("board").document("current_message")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    familyMessage = "Error al cargar mensaje"
                    return@addSnapshotListener
                }
                familyMessage = if (snapshot != null && snapshot.exists()) {
                    snapshot.getString("text") ?: "No hay mensaje."
                } else {
                    "Aún no hay mensajes."
                }
            }
    }

    fun fetchWeather(location: String = "Saltillo") {
        viewModelScope.launch {
            weatherInfo = try {
                val response = apiService.getCurrentWeather(location, "f32b96c8ed2ce5a044e1e49dd20bf6b3")
                val temp = response.main.temp.toInt()
                val description = response.weather.firstOrNull()?.description?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                } ?: "No disponible"

                "$temp°C - $description"
            } catch (e: Exception) {
                e.printStackTrace()
                "Error al cargar el clima"
            }
        }
    }
}
