package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.costcalculator.data.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expense: Expense?, // Додано: може бути null, якщо це нова витрата
    onSave: (Expense) -> Unit, // Тепер передаємо цілий об'єкт
    onNavigateBack: () -> Unit
) {
    var amount by remember { mutableStateOf(expense?.amount?.toString() ?: "") }
    var category by remember { mutableStateOf(expense?.category ?: "") }
    var description by remember { mutableStateOf(expense?.description ?: "") }

    val title = if (expense == null) "Додати витрату" else "Редагувати витрату"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Додати витрату") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        if (amountDouble != null && category.isNotBlank()) {
                            val expenseToSave = expense?.copy( // Якщо редагуємо, копіюємо існуючий об'єкт
                                amount = amountDouble,
                                category = category,
                                description = description
                            ) ?: Expense( // Якщо створюємо новий
                                amount = amountDouble,
                                category = category,
                                description = description
                            )
                            onSave(expenseToSave)
                        }
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Зберегти")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сума*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Категорія*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Опис") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}