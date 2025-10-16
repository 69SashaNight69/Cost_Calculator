package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.costcalculator.ui.components.ExpenseList
import com.example.costcalculator.viewmodel.ExpenseViewModel
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel = viewModel(),
    onExpenseClick: (Long) -> Unit,
    onAddExpenseClick: () -> Unit,
    onManageCategoriesClick: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Калькулятор витрат") },
                actions = { // Додаємо секцію actions
                    IconButton(onClick = onManageCategoriesClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Налаштування категорій")
                    }
                }
            )
        },
        floatingActionButton = { // ДОДАНО: плаваюча кнопка дії
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Filled.Add, contentDescription = "Додати витрату")
            }
        }
    ) { innerPadding ->
        ExpenseList(
            expenses = expenses,
            modifier = Modifier.padding(innerPadding),
            onExpenseClick = onExpenseClick,
            onExpenseSwiped = { expense -> // ДОДАНО: обробка свайпу
                viewModel.deleteExpense(expense)
            }
        )
    }
}