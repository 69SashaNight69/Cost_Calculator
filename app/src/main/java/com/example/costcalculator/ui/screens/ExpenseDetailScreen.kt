package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.costcalculator.data.Expense // Імпортуйте ваш data class
import com.example.costcalculator.utils.toCurrencyFormat // Імпортуйте вашу функцію

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expense: Expense,
    onNavigateBack: () -> Unit // Лямбда для повернення назад
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Деталі витрати") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Повернутись назад"
                        )
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
            Text(text = "Категорія: ${expense.category}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Сума: ${expense.amount.toCurrencyFormat()}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            expense.description?.let {
                Text(text = "Опис: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}