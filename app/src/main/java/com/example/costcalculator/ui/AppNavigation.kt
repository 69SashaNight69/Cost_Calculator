package com.example.costcalculator.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.costcalculator.data.Expense
import com.example.costcalculator.ui.screens.AddEditExpenseScreen
import com.example.costcalculator.ui.screens.CategoryManagementScreen
import com.example.costcalculator.ui.screens.ExpenseDetailScreen
import com.example.costcalculator.ui.screens.ExpenseTrackerScreen
import com.example.costcalculator.viewmodel.ExpenseViewModel

@Composable
fun AppNavigation(viewModel: ExpenseViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "expense_list") {
        composable("expense_list") {
            ExpenseTrackerScreen(
                viewModel = viewModel,
                onExpenseClick = { expenseId ->
                    navController.navigate("expense_detail/$expenseId")
                },
                onAddExpenseClick = {
                    navController.navigate("edit_expense")
                },
                onManageCategoriesClick = {
                    navController.navigate("category_management")
                }
            )
        }

        composable(
            route = "expense_detail/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            var expense by remember { mutableStateOf<Expense?>(null) }

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

        // <-- ТУТ ЗМІНА в логіці onSave
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
                    onSave = { expenseToSave -> // <-- ТУТ ЗМІНА
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

        composable("category_management") {
            CategoryManagementScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}