package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.costcalculator.ui.components.ExpenseList
import com.example.costcalculator.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel = viewModel(),
    onExpenseClick: (Long) -> Unit,
    onAddExpenseClick: () -> Unit,
    onManageCategoriesClick: () -> Unit,
    onManageGroupsClick: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Калькулятор витрат") },
                actions = {
                    IconButton(onClick = onManageGroupsClick) {
                        Icon(Icons.Default.Group, contentDescription = "Налаштування груп")
                    }
                    IconButton(onClick = onManageCategoriesClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Налаштування категорій")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Filled.Add, contentDescription = "Додати витрату")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Нова секція з випадаючими списками для фільтрації
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Фільтр за категоріями
                FilterDropdown(
                    modifier = Modifier.weight(1f),
                    label = "Категорія",
                    items = categories.map { it.name to it.id },
                    selectedItemId = selectedCategoryId,
                    onItemSelected = { viewModel.selectCategory(it) }
                )
                // Фільтр за групами
                FilterDropdown(
                    modifier = Modifier.weight(1f),
                    label = "Група",
                    items = groups.map { it.name to it.id },
                    selectedItemId = selectedGroupId,
                    onItemSelected = { viewModel.selectGroup(it) }
                )
            }

            // Список витрат, який тепер отримує і список груп
            ExpenseList(
                expenses = expenses,
                groups = groups, // Передаємо список груп для відображення назв
                onExpenseClick = onExpenseClick,
                onExpenseSwiped = { expense ->
                    viewModel.deleteExpense(expense)
                }
            )
        }
    }
}

/**
 * Перевикористовуваний Composable-компонент для випадаючого списку-фільтра.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    modifier: Modifier = Modifier,
    label: String,
    items: List<Pair<String, Long>>,
    selectedItemId: Long?,
    onItemSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItemName = items.find { it.second == selectedItemId }?.first ?: "Всі"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItemName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Пункт "Всі" для скидання фільтру
            DropdownMenuItem(
                text = { Text("Всі") },
                onClick = {
                    onItemSelected(null)
                    expanded = false
                }
            )
            // Решта пунктів
            items.forEach { (name, id) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onItemSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}