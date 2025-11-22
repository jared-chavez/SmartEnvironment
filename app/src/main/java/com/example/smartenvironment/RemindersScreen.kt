package com.example.smartenvironment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartenvironment.data.Reminder
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

@Composable
private fun RemindersCard(
    modifier: Modifier = Modifier,
    reminders: List<Reminder>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onToggleCompleted: (String, Boolean) -> Unit
) {
    Card(
        modifier = modifier.height(250.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp) // Espacio para el FAB
            ) {
                Text("Recordatorios", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (reminders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "No reminders",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "¡Todo está al día!",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                "Añade un nuevo recordatorio con el botón +",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(reminders, key = { it.id }) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onDeleteClick = { onDeleteClick(reminder.id) },
                                onToggleCompleted = { isChecked -> onToggleCompleted(reminder.id, isChecked) }
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
    onToggleCompleted: (Boolean) -> Unit
) {
    val statusColor = getStatusColor(reminder.reminderAt?.toDate(), reminder.isCompleted)
    val formattedCreationDate = reminder.createdAt?.toDate()?.let {
        SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(it)
    } ?: ""
    val formattedReminderDate = reminder.reminderAt?.toDate()?.let {
        SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(it)
    } ?: ""

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = reminder.isCompleted, onCheckedChange = onToggleCompleted)

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.text,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = if (reminder.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (reminder.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = "Completado", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Box(modifier = Modifier.size(10.dp).background(statusColor, MaterialTheme.shapes.small))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Creado: $formattedCreationDate")
                        if (formattedReminderDate.isNotEmpty()) {
                            append(" | ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = if (statusColor == MaterialTheme.colorScheme.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)) {
                                append("Límite: $formattedReminderDate")
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar recordatorio", tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun getStatusColor(reminderDate: Date?, isCompleted: Boolean): Color {
    if (isCompleted) return MaterialTheme.colorScheme.primary
    if (reminderDate == null) return Color.Transparent

    val now = Calendar.getInstance().time
    val oneDayInMillis = 24 * 60 * 60 * 1000

    return when {
        now.after(reminderDate) -> MaterialTheme.colorScheme.error // Vencido
        (reminderDate.time - now.time) < oneDayInMillis -> MaterialTheme.colorScheme.tertiary // Advertencia
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) // A tiempo
    }
}
