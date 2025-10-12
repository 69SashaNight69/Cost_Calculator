package com.example.costcalculator.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.costcalculator.data.Expense // Переконайтесь, що цей імпорт правильний
import com.example.costcalculator.ui.screens.AddEditExpenseScreen
import com.example.costcalculator.ui.screens.ExpenseDetailScreen
import com.example.costcalculator.ui.screens.ExpenseTrackerScreen
import com.example.costcalculator.viewmodel.ExpenseViewModel

@Composable
fun AppNavigation(viewModel: ExpenseViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "expense_list") {
        // Маршрут №1: Екран зі списком витрат
        composable("expense_list") {
            ExpenseTrackerScreen(
                viewModel = viewModel,
                onExpenseClick = { expenseId ->
                    // Перехід на екран деталей
                    navController.navigate("expense_detail/$expenseId")
                },
                onAddExpenseClick = {
                    // Перехід на екран додавання
                    navController.navigate("edit_expense")
                }
            )
        }

        // Маршрут №2: Екран деталей витрати (ВИПРАВЛЕНА ВЕРСІЯ)
        composable(
            route = "expense_detail/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            var expense by remember { mutableStateOf<Expense?>(null) }

            // LaunchedEffect безпечно викликає suspend-функцію при першому запуску
            // або коли expenseId змінюється.
            LaunchedEffect(key1 = expenseId) {
                if (expenseId != null) {
                    expense = viewModel.getExpenseById(expenseId)
                }
            }

            // Показуємо екран, тільки коли дані завантажено
            if (expense != null) {
                ExpenseDetailScreen(
                    expense = expense!!,
                    onNavigateBack = { navController.popBackStack() },
                    onEdit = { // Передаємо дію
                        navController.navigate("edit_expense?expenseId=${expense!!.id}")
                    }
                )
            }
            // В іншому випадку нічого не показуємо (або можна додати індикатор завантаження)
        }

        // Маршрут №3: Екран додавання/редагування (НОВИЙ)
        composable(
            route = "edit_expense?expenseId={expenseId}", // робимо ID необов'язковим
            arguments = listOf(navArgument("expenseId") {
                type = NavType.LongType
                defaultValue = -1L // Значення за замовчуванням
            })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            var expense by remember { mutableStateOf<Expense?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(key1 = expenseId) {
                if (expenseId != null && expenseId != -1L) {
                    expense = viewModel.getExpenseById(expenseId)
                }
                isLoading = false
            }

            if (!isLoading) {
                AddEditExpenseScreen(
                    expense = expense,
                    onSave = { expenseToSave ->
                        if (expenseToSave.id == 0L) { // 0L - id за замовчуванням для нового об'єкта
                            viewModel.addExpense(expenseToSave)
                        } else {
                            viewModel.updateExpense(expenseToSave)
                        }
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}