package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    onQrCodeScanned: (String) -> Unit
) {
    var qrText by remember { mutableStateOf("") }
    val sampleQrFromCheck = "t=20240101T120000&s=250.75&fn=1234567890&i=1234&fp=123456789&n=1"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Імітація сканера") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Вставте сюди дані QR-коду:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = qrText,
                onValueChange = { qrText = it },
                label = { Text("Дані з QR") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                onQrCodeScanned(qrText)
            }) {
                Text("Надіслати результат")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                qrText = sampleQrFromCheck
            }) {
                Text("Вставити приклад з чеку")
            }
        }
    }
}