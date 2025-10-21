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
import com.example.costcalculator.data.ExpenseGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expense: Expense?,
    categories: List<Category>,
    groups: List<ExpenseGroup>,
    onSave: (Expense) -> Unit,
    onNavigateBack: () -> Unit
) {
    var amount by remember { mutableStateOf(expense?.amount?.toString() ?: "") }
    var selectedCategory by remember {
        mutableStateOf(categories.find { it.name == expense?.category } ?: categories.firstOrNull())
    }
    // Стан для обраної групи
    var selectedGroup by remember {
        mutableStateOf(groups.find { it.id == expense?.groupId })
    }
    var description by remember { mutableStateOf(expense?.description ?: "") }

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
                        if (amountDouble != null && selectedCategory != null) {
                            val expenseToSave = expense?.copy(
                                amount = amountDouble,
                                category = selectedCategory!!.name,
                                description = description,
                                groupId = selectedGroup?.id // ДОДАНО: зберігаємо ID групи
                            ) ?: Expense(
                                amount = amountDouble,
                                category = selectedCategory!!.name,
                                description = description,
                                groupId = selectedGroup?.id // ДОДАНО: зберігаємо ID групи
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

            // Список для Категорій (без змін)
            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Оберіть категорію*",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категорія*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // ДОДАНО: Новий випадаючий список для Груп
            var groupExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = groupExpanded,
                onExpandedChange = { groupExpanded = !groupExpanded }
            ) {
                OutlinedTextField(
                    value = selectedGroup?.name ?: "Без групи", // Текст за замовчуванням
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Група (опціонально)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = groupExpanded,
                    onDismissRequest = { groupExpanded = false }
                ) {
                    // Додаємо пункт, щоб можна було скасувати вибір групи
                    DropdownMenuItem(
                        text = { Text("Без групи") },
                        onClick = {
                            selectedGroup = null
                            groupExpanded = false
                        }
                    )
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroup = group
                                groupExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Опис") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}