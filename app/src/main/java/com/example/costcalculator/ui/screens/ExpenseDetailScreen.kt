package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn // Імпортуйте іконку
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.costcalculator.data.Expense
import com.example.costcalculator.utils.toCurrencyFormat
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expense: Expense,
    groupName: String?,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Деталі витрати") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Редагувати")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Категорія: ${expense.category}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Сума: ${expense.amount.toCurrencyFormat()}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (groupName != null) {
                Text(
                    text = "Група: $groupName",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            expense.description?.takeIf { it.isNotBlank() }?.let {
                Text(text = "Опис: $it", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp)) // Додатковий відступ після опису
            }

            if (expense.latitude != null && expense.longitude != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Місцезнаходження",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp) // Додаємо відступ справа від іконки
                    )
                    Column {
                        Text(
                            text = "Місцезнаходження збережено",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold // Робимо текст жирнішим
                        )
                        // Форматуємо координати до 4 знаків після коми для компактності
                        val formattedLat = "%.4f".format(expense.latitude)
                        val formattedLon = "%.4f".format(expense.longitude)
                        Text(
                            text = "($formattedLat, $formattedLon)",
                            style = MaterialTheme.typography.bodySmall, // Робимо шрифт меншим
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Трохи сіріший колір
                        )
                    }
                }
            }
        }
    }
}