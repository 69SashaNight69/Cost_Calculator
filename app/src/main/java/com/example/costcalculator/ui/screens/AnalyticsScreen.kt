package com.example.costcalculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.costcalculator.data.PieSliceData
import com.example.costcalculator.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    // Отримуємо дані з ViewModel
    val chartDataList by viewModel.chartData.collectAsState()

    // Кольори для секторів
    val chartColors = listOf(
        Color(0xFF8A2BE2), Color(0xFF5F9EA0), Color(0xFFD2691E),
        Color(0xFFFF7F50), Color(0xFF6495ED), Color(0xFFDC143C)
    )

    // Підготовка даних: рахуємо загальну суму та кути
    val totalValue = chartDataList.sumOf { it.value.toDouble() }.toFloat()
    val slices = chartDataList.mapIndexed { index, data ->
        val sweepAngle = if (totalValue == 0f) 0f else (data.value / totalValue) * 360f
        PieSliceData(
            value = data.value,
            label = data.label,
            color = chartColors[index % chartColors.size],
            sweepAngle = sweepAngle
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аналітика витрат") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Змінив на Top, щоб діаграма була вище
        ) {
            if (chartDataList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))

                // --- Власна Кругова Діаграма ---
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f // Починаємо з 12 години
                        val strokeWidth = 50.dp.toPx()

                        slices.forEach { slice ->
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = slice.sweepAngle,
                                useCenter = false, // false для "пончика" (Donut chart), true для пирога
                                style = Stroke(width = strokeWidth),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                                topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                            )
                            startAngle += slice.sweepAngle
                        }
                    }
                    // Текст всередині (опціонально)
                    Text(
                        text = "%.0f грн".format(totalValue),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Легенда ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEach { slice ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(slice.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = slice.label,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "%.2f грн".format(slice.value),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Немає даних для відображення. Додайте хоча б одну витрату.")
                }
            }
        }
    }
}