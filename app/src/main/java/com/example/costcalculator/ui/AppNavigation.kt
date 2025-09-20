package com.example.costcalculator.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                    navController.navigate("expense_detail/$expenseId")
                }
            )
        }

        // Маршрут №2: Екран деталей витрати
        composable(
            route = "expense_detail/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            val expense = expenseId?.let { viewModel.getExpenseById(it) }

            if (expense != null) {
                ExpenseDetailScreen(
                    expense = expense,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                // Можна показати екран помилки або повернутись назад
                navController.popBackStack()
            }
        }
    }
}