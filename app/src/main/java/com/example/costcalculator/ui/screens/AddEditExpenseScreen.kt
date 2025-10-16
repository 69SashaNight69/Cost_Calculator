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
import com.example.costcalculator.data.Category
import com.example.costcalculator.data.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expense: Expense?,
    categories: List<Category>, // Список доступних категорій
    onSave: (Expense) -> Unit, // Функція для збереження, тепер приймає цілий об'єкт
    onNavigateBack: () -> Unit
) {
    // Стан для полів вводу
    var amount by remember { mutableStateOf(expense?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(expense?.description ?: "") }

    // Стан для випадаючого списку.
    // Знаходимо категорію, яка відповідає назві у витраті, або беремо першу зі списку.
    var selectedCategory by remember {
        mutableStateOf(
            if (expense != null) categories.find { it.name == expense.category }
            else categories.firstOrNull()
        )
    }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Динамічний заголовок екрану
    val title = if (expense == null) "Додати витрату" else "Редагувати витрату"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        // Перевіряємо, що сума введена коректно і категорія обрана
                        if (amountDouble != null && selectedCategory != null) {
                            val expenseToSave = expense?.copy( // Якщо редагуємо, оновлюємо існуючий
                                amount = amountDouble,
                                category = selectedCategory!!.name, // Беремо назву з обраної категорії
                                description = description
                            ) ?: Expense( // Якщо створюємо новий
                                amount = amountDouble,
                                category = selectedCategory!!.name, // Беремо назву з обраної категорії
                                description = description
                            )
                            onSave(expenseToSave) // Відправляємо об'єкт на збереження
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
            // Поле для суми (без змін)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сума*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // ВИПАДАЮЧИЙ СПИСОК ЗАМІСТЬ ТЕКСТОВОГО ПОЛЯ
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    // Показуємо назву обраної категорії або підказку
                    value = selectedCategory?.name ?: "Оберіть категорію*",
                    onValueChange = {}, // Поле не можна редагувати вручну
                    readOnly = true,
                    label = { Text("Категорія*") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor() // Цей модифікатор прив'язує меню до поля
                        .fillMaxWidth()
                )

                // Саме меню, яке з'являється при кліку
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category // Оновлюємо обрану категорію
                                isDropdownExpanded = false // Закриваємо меню
                            }
                        )
                    }
                }
            }

            // Поле для опису (без змін)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Опис") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}