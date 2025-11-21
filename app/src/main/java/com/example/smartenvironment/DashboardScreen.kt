package com.example.smartenvironment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartenvironment.ui.theme.fourthColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    if (viewModel.showLocationDialog) {
        LocationInputDialog(
            onConfirm = { viewModel.fetchWeather(it); viewModel.closeLocationDialog() },
            onDismiss = { viewModel.closeLocationDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(text = "Hola, Familia", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        // Fila de Dispositivos
        Row(modifier = Modifier.fillMaxWidth()) {
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

        Row(modifier = Modifier.fillMaxWidth()) {
            // Tarjeta Clima
            Card(
                onClick = { viewModel.openLocationDialog() },
                modifier = Modifier.weight(1f).height(150.dp).padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Clima Hoy")
                    Text(viewModel.weatherInfo, style = MaterialTheme.typography.titleLarge)
                }
            }
            // Tarjeta Mensajes
            Card(
                onClick = { /* Acción futura para editar mensaje */ },
                modifier = Modifier.weight(2f).height(150.dp).padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pizarrón Familiar")
                    Text(viewModel.familyMessage)
                }
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
        modifier = modifier.height(150.dp).padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title)
            Text(
                text = if (isOn) "ENCENDIDO" else "APAGADO",
                color = if (isOn) fourthColor else Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationInputDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.width(400.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Cambiar Ubicación", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Escribe una ciudad") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { onConfirm(text) }) { Text("Confirmar") }
                }
            }
        }
    }
}
