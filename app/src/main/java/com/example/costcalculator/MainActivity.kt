package com.example.costcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.costcalculator.ui.theme.CostCalculatorTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            Log.d("ExpenseApp", "Початок фонового завдання...")
            // Імітуємо затримку в 3 секунди
            delay(3000L)
            Log.d("ExpenseApp", "Фонове завдання завершено!")
        }
        setContent {
            CostCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Замінюємо Greeting на MainScreen
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
data class Expense(
    val id: Long,          // Унікальний ідентифікатор витрати
    val amount: Double,    // Сума витрати
    val category: String,  // Категорія (наприклад, "Їжа", "Транспорт")
    val description: String? // Опис (необов'язковий)
)

// Створюємо extension-функцію для класу Double
fun Double.toCurrencyFormat(): String {
    return "%.2f грн".format(this)
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Створюємо об'єкт (екземпляр) нашого класу Expense
    val sampleExpense = Expense(
        id = 1L,
        amount = 250.75,
        category = "Продукти",
        description = null // Приклад використання null
    )

    // Використовуємо нашу нову extension-функцію
    Text(
        text = "Категорія: ${sampleExpense.category}, Сума: ${sampleExpense.amount.toCurrencyFormat()}",
        modifier = modifier
    )
}

@Composable
fun ExpenseItem(expense: Expense, modifier: Modifier = Modifier) { // Додали modifier як параметр
    Column(
        modifier = modifier // Використовуємо переданий modifier
            .fillMaxWidth() // Розтягуємо на всю ширину
            .padding(16.dp) // Додаємо відступи 16dp з усіх боків
    ) {
        Text(
            text = expense.category,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = expense.amount.toCurrencyFormat(),
            fontSize = 16.sp
        )
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) { // Додали modifier
    var clicksCount by remember { mutableStateOf(0) }

    val sampleExpense = Expense(
        id = 1L,
        amount = 250.75,
        category = "Продукти",
        description = "Покупка в супермаркеті"
    )

    // Використовуємо переданий modifier для Column
    Column(modifier = modifier.padding(16.dp)) {
        ExpenseItem(expense = sampleExpense)

        Button(onClick = {
            clicksCount++
            Log.d("ComposeState", "Button clicked! Count: $clicksCount")
        }) {
            Text(text = "Натиснуто: $clicksCount разів")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CostCalculatorTheme {
        Greeting("Android")
    }
}