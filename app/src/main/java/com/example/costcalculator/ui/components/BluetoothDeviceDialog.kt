package com.example.costcalculator.ui.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("MissingPermission") // Дозволи перевіряються перед викликом
@Composable
fun BluetoothDeviceDialog(
    devices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Оберіть пристрій") },
        text = {
            if (devices.isEmpty()) {
                Text("Не знайдено спарених пристроїв. Будь ласка, спершу спарте пристрої в налаштуваннях Android.")
            } else {
                LazyColumn {
                    items(devices) { device ->
                        Text(
                            text = device.name ?: "Невідомий пристрій",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDeviceClick(device) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}