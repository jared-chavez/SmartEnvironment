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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.example.smartenvironment.data.AlertData
import com.example.smartenvironment.data.Reminder
import com.example.smartenvironment.data.WeatherData
import com.example.smartenvironment.data.WeatherIconType
import com.example.smartenvironment.ui.theme.firstColor
import com.example.smartenvironment.ui.theme.fourthColor
import com.example.smartenvironment.ui.theme.secondColor
import com.example.smartenvironment.ui.theme.thirdColor
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTvMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    var isSidebarVisible by remember { mutableStateOf(true) }
    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarVisible) 200.dp else 0.dp,
        label = "sidebarWidth"
    )

    if (viewModel.showLocationDialog) {
        LocationSelectorDialog(
            onConfirm = {
                viewModel.updateWeatherLocation(it)
                viewModel.closeLocationDialog()
            },
            onDismiss = { viewModel.closeLocationDialog() }
        )
    }

    if (viewModel.showAddReminderDialog) {
        AddReminderDialog(
            onConfirm = {
                text, date -> viewModel.addReminder(text, date)
                viewModel.closeAddReminderDialog()
            },
            onDismiss = { viewModel.closeAddReminderDialog() }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(firstColor)) {
        Row(Modifier.fillMaxSize()) {
            // Side Panel
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(sidebarWidth)
                    .background(secondColor)
                    .padding(if (isSidebarVisible) 24.dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSidebarVisible) {
                    Text("Info", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                IconButton(onClick = { isSidebarVisible = !isSidebarVisible }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Sidebar",
                        tint = thirdColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Hola, Familia", style = MaterialTheme.typography.headlineLarge, color = thirdColor)
                Spacer(modifier = Modifier.height(20.dp))

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta Clima
                    WeatherCard(
                        modifier = Modifier.weight(1f),
                        weatherData = viewModel.weatherData,
                        location = viewModel.weatherLocation,
                        statusMessage = viewModel.weatherStatusMessage,
                        onClick = { viewModel.openLocationDialog() }
                    )

                    // Tarjeta Mensajes
                    RemindersCard(
                        modifier = Modifier.weight(2f),
                        reminders = viewModel.reminders,
                        onAddClick = { viewModel.openAddReminderDialog() },
                        onDeleteClick = { viewModel.deleteReminder(it) },
                        onToggleCompleted = { id, completed -> viewModel.toggleReminderCompleted(id, completed) }
                    )
                }
            }
        }
        AlertHost(alerts = viewModel.alerts, onDismiss = { viewModel.dismissAlert(it) })
    }
}

@Composable
fun AlertHost(modifier: Modifier = Modifier, alerts: List<AlertData>, onDismiss: (Long) -> Unit) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.align(Alignment.TopEnd)) {
            items(alerts) { alert ->
                AlertCard(alert = alert, onDismiss = { onDismiss(alert.id) })
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AlertCard(alert: AlertData, onDismiss: () -> Unit) {
    LaunchedEffect(alert.id) {
        delay(5000) // 5 segundos
        onDismiss()
    }
    Card(
        onClick = { onDismiss() },
        modifier = Modifier
            .padding(vertical = 4.dp)
            .width(280.dp),
        colors = CardDefaults.colors(containerColor = alert.type.color)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = alert.message, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Delete, contentDescription = "Cerrar alerta", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
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
        colors = CardDefaults.colors(containerColor = Color.White),
        border = CardDefaults.border(border = Border(BorderStroke(2.dp, thirdColor)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(40.dp), tint = secondColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = thirdColor)
            Text(
                text = if (isOn) "ENCENDIDO" else "APAGADO",
                color = if (isOn) fourthColor else Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun RemindersCard(
    modifier: Modifier = Modifier,
    reminders: List<Reminder>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onToggleCompleted: (String, Boolean) -> Unit
) {
    Card(
        onClick = { /* No action for now */ },
        modifier = modifier.height(250.dp),
        colors = CardDefaults.colors(containerColor = Color.White),
        border = CardDefaults.border(border = Border(BorderStroke(2.dp, thirdColor)))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp) // Espacio para el FAB
            ) {
                Text("Recordatorios", color = thirdColor, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (reminders.isEmpty()) {
                    Text("No hay recordatorios.", style = MaterialTheme.typography.bodyLarge, color = thirdColor)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(reminders) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onDeleteClick = { onDeleteClick(reminder.id) },
                                onToggleCompleted = { onToggleCompleted(reminder.id, !reminder.isCompleted) }
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = secondColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar recordatorio")
            }
        }
    }
}

@Composable
private fun ReminderItem(
    reminder: Reminder,
    onDeleteClick: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    val statusColor = getStatusColor(reminder.reminderAt?.toDate(), reminder.isCompleted)
    val formattedCreationDate = reminder.createdAt?.toDate()?.let {
        SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(it)
    } ?: ""
    val formattedReminderDate = reminder.reminderAt?.toDate()?.let {
        SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(it)
    } ?: ""

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = reminder.isCompleted, onCheckedChange = { onToggleCompleted() })

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (reminder.isCompleted) Color.Gray else thirdColor,
                textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (reminder.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = "Completado", tint = Color.Green, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Box(modifier = Modifier.size(10.dp).background(statusColor, MaterialTheme.shapes.small))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Creado: $formattedCreationDate${if (formattedReminderDate.isNotEmpty()) " | Límite: $formattedReminderDate" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar recordatorio", tint = Color.Gray)
        }
    }
}

private fun getStatusColor(reminderDate: Date?, isCompleted: Boolean): Color {
    if (isCompleted) return Color.Green
    if (reminderDate == null) return Color.Transparent

    val now = Calendar.getInstance().time
    val oneDayInMillis = 24 * 60 * 60 * 1000

    return when {
        now.after(reminderDate) -> Color.Red // Vencido
        (reminderDate.time - now.time) < oneDayInMillis -> Color.Yellow // Advertencia
        else -> Color(0xFF8BC34A) // A tiempo (verde más suave)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun WeatherCard(
    modifier: Modifier = Modifier,
    weatherData: WeatherData?,
    location: String,
    statusMessage: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(150.dp),
        colors = CardDefaults.colors(containerColor = Color.White),
        border = CardDefaults.border(border = Border(BorderStroke(2.dp, thirdColor)))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = thirdColor,
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
                        WeatherIconType.SUNNY -> Icons.Default.WbSunny to Color(0xFFFFC107) // Amarillo
                        WeatherIconType.CLOUDY -> Icons.Default.Cloud to Color.Gray
                        WeatherIconType.RAINY -> Icons.Filled.WbCloudy to Color(0xFF2196F3) // Azul
                        WeatherIconType.PARTLY_CLOUDY -> Icons.Default.WbCloudy to Color.LightGray
                        WeatherIconType.NIGHT -> Icons.Default.NightsStay to Color.LightGray
                        WeatherIconType.UNKNOWN -> Icons.Default.WbSunny to secondColor // Color por defecto
                    }
                    Icon(imageVector = icon, contentDescription = "Weather Icon", modifier = Modifier.size(40.dp), tint = color)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${weatherData.temperature}°C", style = MaterialTheme.typography.headlineSmall, color = thirdColor)
                    Text(weatherData.description, style = MaterialTheme.typography.bodySmall, color = thirdColor)
                } else {
                    Text(statusMessage, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = thirdColor)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
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
private fun LocationSelectorDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val coahuilaCities = listOf("Saltillo", "Torreón", "Monclova", "Piedras Negras", "Acuña", "Ramos Arizpe", "Sabinas", "Múzquiz", "Parras de la Fuente", "San Pedro")
    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(coahuilaCities.first()) }

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
