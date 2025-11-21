package com.example.smartenvironment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.example.smartenvironment.data.Reminder
import com.example.smartenvironment.ui.theme.secondColor
import com.example.smartenvironment.ui.theme.thirdColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RemindersScreen(viewModel: DashboardViewModel) {
    RemindersCard(
        modifier = Modifier.fillMaxSize(),
        reminders = viewModel.reminders,
        onAddClick = { viewModel.openAddReminderDialog() },
        onDeleteClick = { viewModel.deleteReminder(it) },
        onToggleCompleted = { id, completed -> viewModel.toggleReminderCompleted(id, completed) }
    )
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
