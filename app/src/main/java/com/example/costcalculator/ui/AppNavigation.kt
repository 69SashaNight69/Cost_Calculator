package com.example.costcalculator.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.costcalculator.data.AppDatabase
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.repository.ExpenseRepository
import com.example.costcalculator.ui.screens.AddEditExpenseScreen
import com.example.costcalculator.ui.screens.CategoryManagementScreen
import com.example.costcalculator.ui.screens.ExpenseDetailScreen
import com.example.costcalculator.ui.screens.ExpenseTrackerScreen
import com.example.costcalculator.viewmodel.ExpenseViewModel
import com.example.costcalculator.viewmodel.ExpenseViewModelFactory

@Composable
fun AppNavigation() { // ЗМІНА 1: Прибрали viewModel з параметрів функції

    // ЗМІНА 2: Створюємо всі залежності прямо тут
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = ExpenseRepository(database.expenseDao(), database.categoryDao())
    val viewModelFactory = ExpenseViewModelFactory(repository)

    // Тепер viewModel буде створюватися за допомогою нашої фабрики
    // Це забезпечує, що всі екрани використовують один і той самий екземпляр
    val viewModel: ExpenseViewModel = viewModel(factory = viewModelFactory)

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "expense_list") {
        // Маршрут №1: Екран зі списком витрат
        composable("expense_list") {
            // ЗМІНА 3: Тепер не потрібно передавати viewModel як параметр,
            // оскільки кожна composable-функція може отримати його сама.
            // Але для ясності ми передаємо його явно.
            ExpenseTrackerScreen(
                viewModel = viewModel,
                onExpenseClick = { expenseId ->
                    navController.navigate("expense_detail/$expenseId")
                },
                onAddExpenseClick = {
                    navController.navigate("edit_expense") // Перехід без ID
                },
                onManageCategoriesClick = {
                    navController.navigate("category_management")
                }
            )
        }

        // Маршрут №2: Екран деталей витрати
        composable(
            route = "expense_detail/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            var expense by remember { mutableStateOf<Expense?>(null) }

            // Ця частина залишається без змін, вона працює з viewModel
            LaunchedEffect(key1 = expenseId) {
                if (expenseId != null) {
                    expense = viewModel.getExpenseById(expenseId)
                }
            }

            if (expense != null) {
                ExpenseDetailScreen(
                    expense = expense!!,
                    onNavigateBack = { navController.popBackStack() },
                    onEdit = {
                        navController.navigate("edit_expense?expenseId=${expense!!.id}")
                    }
                )
            }
        }

        // Маршрут №3: Екран додавання/редагування
        composable(
            route = "edit_expense?expenseId={expenseId}",
            arguments = listOf(navArgument("expenseId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            var expense by remember { mutableStateOf<Expense?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            val categories by viewModel.categories.collectAsState()

            LaunchedEffect(key1 = expenseId) {
                if (expenseId != null && expenseId != -1L) {
                    expense = viewModel.getExpenseById(expenseId)
                }
                isLoading = false
            }

            if (!isLoading) {
                AddEditExpenseScreen(
                    expense = expense,
                    categories = categories,
                    onSave = { expenseToSave ->
                        if (expenseToSave.id == 0L) {
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

        // Маршрут №4: Управління категоріями
        composable("category_management") {
            CategoryManagementScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}