package com.example.costcalculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.example.costcalculator.data.PieSliceData // Ваш імпорт
import com.example.costcalculator.ui.components.KpiCard // Новий імпорт для карток
import com.example.costcalculator.utils.toCurrencyFormat // Новий імпорт для форматування
import com.example.costcalculator.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    // --- ДАНІ (ВАШ КОД) ---
    val chartDataList by viewModel.chartData.collectAsState()

    // --- ДАНІ (НОВИЙ КОД ДЛЯ KPI) ---
    val totalSpent by viewModel.totalSpent.collectAsState()
    val transactionCount by viewModel.transactionCount.collectAsState()
    val averageTransaction by viewModel.averageTransaction.collectAsState()

    // --- ПІДГОТОВКА ДАНИХ ДЛЯ ДІАГРАМИ (ВАШ КОД) ---
    val chartColors = listOf(
        Color(0xFF8A2BE2), Color(0xFF5F9EA0), Color(0xFFD2691E),
        Color(0xFFFF7F50), Color(0xFF6495ED), Color(0xFFDC143C)
    )
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
        topBar = { /* ВАШ КОД - без змін */ }
    ) { innerPadding ->
        // --- ЗМІНА: Додаємо Column з прокруткою ---
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // Горизонтальні відступи для всього екрану
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Додаємо можливість прокрутки
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (chartDataList.isNotEmpty()) {
                // --- НОВА СЕКЦІЯ: KPI-картки ---
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Загальна статистика",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    KpiCard(
                        title = "Всього витрачено",
                        value = totalSpent.toCurrencyFormat(),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Сер. чек",
                        value = averageTransaction.toCurrencyFormat(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                KpiCard(
                    title = "Кількість транзакцій",
                    value = transactionCount.toString(),
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(modifier = Modifier.padding(vertical = 24.dp)) // Розділювач

                // --- ВАША СЕКЦІЯ: Діаграма та легенда ---
                Text(
                    text = "Розподіл за категоріями",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        val strokeWidth = 50.dp.toPx()
                        slices.forEach { slice ->
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = slice.sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                                topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                            )
                            startAngle += slice.sweepAngle
                        }
                    }
                    Text(
                        text = "%.0f грн".format(totalValue),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEach { slice ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.size(16.dp).background(slice.color, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = slice.label, modifier = Modifier.weight(1f))
                            Text(text = "%.2f грн".format(slice.value), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Додатковий відступ знизу

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