package com.example.smartenvironment

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartenvironment.data.AlertData
import com.example.smartenvironment.data.WeatherData
import com.example.smartenvironment.data.WeatherIconType
import com.example.smartenvironment.ui.theme.SmartEnvironmentTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    SmartEnvironmentTheme {
        var isSidebarVisible by remember { mutableStateOf(true) }
        val sidebarWidth by animateDpAsState(
            targetValue = if (isSidebarVisible) 250.dp else 0.dp,
            label = "sidebarWidth"
        )

        if (viewModel.showLocationDialog) {
            LocationSelectorDialog(
                currentLocation = viewModel.weatherLocation,
                onConfirm = {
                    viewModel.updateWeatherLocation(it)
                    viewModel.closeLocationDialog()
                },
                onDismiss = { viewModel.closeLocationDialog() }
            )
        }

        if (viewModel.showAddReminderDialog) {
            AddReminderDialog(
                onConfirm = { text, date ->
                    viewModel.addReminder(text, date)
                    viewModel.closeAddReminderDialog()
                },
                onDismiss = { viewModel.closeAddReminderDialog() }
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Row(Modifier.fillMaxSize()) {
                // Side Panel
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(sidebarWidth)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(if (isSidebarVisible) 16.dp else 0.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (isSidebarVisible) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Spacer(modifier = Modifier.height(72.dp))
                            TextButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                                Text("Inicio", color = MaterialTheme.colorScheme.onSecondary, textAlign = TextAlign.Start)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.navigateTo(Screen.APPLIANCES) }) {
                                Text("Electrodomésticos", color = MaterialTheme.colorScheme.onSecondary, textAlign = TextAlign.Start)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.navigateTo(Screen.REMINDERS) }) {
                                Text("Recordatorios", color = MaterialTheme.colorScheme.onSecondary, textAlign = TextAlign.Start)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.navigateTo(Screen.WEATHER) }) {
                                Text("Clima", color = MaterialTheme.colorScheme.onSecondary, textAlign = TextAlign.Start)
                            }
                        }
                    }
                }

                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { isSidebarVisible = !isSidebarVisible }) {
                            Icon(
                                imageVector = if (isSidebarVisible) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                                contentDescription = "Toggle Sidebar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = viewModel.currentScreen.title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))

                        IconButton(onClick = { viewModel.navigateTo(Screen.ALERTS) }) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Centro de Notificaciones", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    when (viewModel.currentScreen) {
                        Screen.HOME -> HomeScreen(viewModel)
                        Screen.APPLIANCES -> AppliancesScreen(viewModel)
                        Screen.REMINDERS -> RemindersScreen(viewModel)
                        Screen.WEATHER -> WeatherScreen(viewModel)
                        Screen.ALERTS -> AlertsScreen(alerts = viewModel.alerts, onDismissAlert = { viewModel.dismissAlert(it) })
                    }
                }
            }
            // AlertHost para mostrar pop-ups temporales
            AlertHost(alerts = viewModel.alerts, onDismiss = { viewModel.dismissAlert(it) })
        }
    }
}

@Composable
fun HomeScreen(viewModel: DashboardViewModel) {
    Column {
        // Fila de Dispositivos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeviceCard(
                modifier = Modifier.weight(1f),
                title = "Luz Sala",
                icon = Icons.Default.Lightbulb,
                isOn = viewModel.lightStatus,
                onClick = { viewModel.toggleLightStatus() }
            )
            DeviceCard(
                modifier = Modifier.weight(1f),
                title = "Bocina BT",
                icon = Icons.Default.Bluetooth,
                isOn = viewModel.bluetoothStatus,
                onClick = { viewModel.toggleBluetoothStatus() }
            )
            DeviceCard(
                modifier = Modifier.weight(1f),
                title = "Cafetera",
                icon = Icons.Default.Coffee,
                isOn = viewModel.coffeeMakerStatus,
                onClick = { viewModel.toggleCoffeeMakerStatus() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        WeatherCard(
            modifier = Modifier.fillMaxWidth(),
            weatherData = viewModel.weatherData,
            location = viewModel.weatherLocation,
            statusMessage = viewModel.weatherStatusMessage,
            onClick = { viewModel.openLocationDialog() }
        )
    }
}

// Componente para manejar la aparición de pop-ups de alerta
@Composable
fun AlertHost(modifier: Modifier = Modifier, alerts: List<AlertData>, onDismiss: (String) -> Unit) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.align(Alignment.TopEnd)) {
            items(alerts, key = { it.id }) { alert ->
                AlertCard(alert = alert, onDismiss = { onDismiss(alert.id) })
            }
        }
    }
}

// Card de alerta para el pop-up que se desvanece solo
@Composable
fun AlertCard(alert: AlertData, onDismiss: () -> Unit) {
    LaunchedEffect(alert.id) {
        delay(5000) // Desaparece después de 5 segundos
        onDismiss()
    }
    Card(
        onClick = { onDismiss() },
        modifier = Modifier
            .padding(vertical = 4.dp)
            .width(280.dp),
        colors = CardDefaults.cardColors(containerColor = alert.type.color)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = alert.message, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar alerta", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isOn: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = if (isOn) "ENCENDIDO" else "APAGADO",
                color = if (isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    weatherData: WeatherData?,
    location: String,
    statusMessage: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (weatherData != null) {
                    val (icon, color) = when (weatherData.iconType) {
                        WeatherIconType.SUNNY -> Icons.Default.WbSunny to Color(0xFFFFC107)
                        WeatherIconType.CLOUDY -> Icons.Default.WbCloudy to Color.Gray
                        WeatherIconType.RAINY -> Icons.Default.WbCloudy to Color(0xFF2196F3)
                        WeatherIconType.PARTLY_CLOUDY -> Icons.Default.WbCloudy to Color.LightGray
                        WeatherIconType.NIGHT -> Icons.Default.NightsStay to MaterialTheme.colorScheme.secondary
                        WeatherIconType.UNKNOWN -> Icons.Default.WbSunny to MaterialTheme.colorScheme.secondary
                    }
                    Icon(imageVector = icon, contentDescription = "Weather Icon", modifier = Modifier.size(40.dp), tint = color)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${weatherData.temperature}°C", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text(weatherData.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                } else {
                    Text(statusMessage, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onConfirm: (String, Date?) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var reminderDateTime by remember { mutableStateOf<Calendar?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.width(400.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Nuevo Recordatorio", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Escribe tu nota...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(reminderDateTime?.time?.let { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it) } ?: "Seleccionar Fecha y Hora")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { onConfirm(text, reminderDateTime?.time) }) { Text("Guardar") }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    datePickerState.selectedDateMillis?.let { cal.timeInMillis = it }
                    reminderDateTime = cal
                    showDatePicker = false
                    showTimePicker = true // Muestra el TimePicker después de seleccionar la fecha
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TimePicker(state = timePickerState)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
                        TextButton(onClick = {
                            reminderDateTime?.let {
                                it.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                it.set(Calendar.MINUTE, timePickerState.minute)
                            }
                            showTimePicker = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectorDialog(
    currentLocation: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val coahuilaCities = listOf("Saltillo", "Torreón", "Monclova", "Piedras Negras", "Acuña", "Ramos Arizpe", "Sabinas", "Múzquiz", "Parras de la Fuente", "San Pedro")
    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(currentLocation) }

    LaunchedEffect(currentLocation) {
        selectedCity = currentLocation
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.width(300.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Selecciona una Ciudad", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))

                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(selectedCity, style = MaterialTheme.typography.bodyLarge)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        coahuilaCities.forEach { city ->
                            DropdownMenuItem(text = { Text(city) }, onClick = { 
                                selectedCity = city
                                expanded = false
                             })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(selectedCity) }) { Text("Confirmar") }
                }
            }
        }
    }
}
