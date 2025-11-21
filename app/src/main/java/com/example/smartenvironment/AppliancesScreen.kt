package com.example.smartenvironment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.example.smartenvironment.ui.theme.fourthColor
import com.example.smartenvironment.ui.theme.secondColor
import com.example.smartenvironment.ui.theme.thirdColor

@Composable
fun AppliancesScreen(viewModel: DashboardViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp) // Espacio para el botón de menú
    ) {
        Text(text = "Electrodomésticos", style = MaterialTheme.typography.headlineLarge, color = thirdColor)
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
        colors = CardDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.White),
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
                color = if (isOn) fourthColor else androidx.compose.ui.graphics.Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
