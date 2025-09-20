package com.example.costcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.costcalculator.ui.theme.CostCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CostCalculatorTheme {
                ExpenseTrackerScreen()
            }
        }
    }
}

data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val description: String?
)

fun Double.toCurrencyFormat(): String {
    return "%.2f грн".format(this)
}

@Composable
fun ExpenseItem(expense: Expense, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Трохи змінив відступи для кращого вигляду
    ) {
        Text(
            text = expense.category,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        // Додамо опис, якщо він є
        expense.description?.let {
            Text(
                text = it,
                fontSize = 14.sp
            )
        }
        Text(
            text = expense.amount.toCurrencyFormat(),
            fontSize = 16.sp
        )
    }
}

@Composable
fun ExpenseList(expenses: List<Expense>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(expenses) { expense ->
            ExpenseItem(expense = expense)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(modifier: Modifier = Modifier) {
    // Наші тестові дані залишаються тут
    val expenses = listOf(
        Expense(1L, 75.50, "Продукти", "Хліб, молоко"),
        Expense(2L, 1200.00, "Оренда", "Платіж за квартиру"),
        Expense(3L, 40.00, "Транспорт", "Квиток на автобус"),
        Expense(4L, 350.25, "Розваги", "Квитки в кіно"),
        Expense(5L, 150.00, "Кафе", "Кава та десерт"),
        Expense(6L, 80.00, "Транспорт", "Поповнення проїзного"),
        Expense(7L, 500.00, "Одяг", "Нова футболка")
    )

    // Ми повертаємо topBar всередину нашого Scaffold
    Scaffold(
        modifier = modifier, // Modifier тут залишається
        topBar = {
            TopAppBar(
                title = { Text("Калькулятор витрат") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        // Контент залишається таким самим
        ExpenseList(
            expenses = expenses,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseTrackerScreenPreview() {
    CostCalculatorTheme {
        ExpenseTrackerScreen()
    }
}