package com.example.smartenvironment

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WeatherScreen(viewModel: DashboardViewModel) {
    WeatherCard(
        modifier = Modifier.fillMaxWidth(),
        weatherData = viewModel.weatherData,
        location = viewModel.weatherLocation,
        statusMessage = viewModel.weatherStatusMessage,
        onClick = { viewModel.openLocationDialog() }
    )
}
