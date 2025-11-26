package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.costcalculator.viewmodel.ExpenseViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.costcalculator.data.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    // Не хвилюйтеся, якщо `expensesWithLocation` підсвічується червоним.
    // Ми створимо його на наступному кроці.
    val expensesWithLocation: List<Expense> by viewModel.expensesWithLocation.collectAsState()

    // Початкова позиція камери (центр Києва)
    val kyiv = LatLng(50.4501, 30.5234)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kyiv, 10f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Карта витрат") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            expensesWithLocation.forEach { expense ->
                // Перевіряємо, що координати існують
                if (expense.latitude != null && expense.longitude != null) {
                    Marker(
                        state = MarkerState(position = LatLng(expense.latitude, expense.longitude)),
                        title = "${expense.category}: ${expense.amount} грн",
                        snippet = expense.description
                    )
                }
            }
        }
    }
}