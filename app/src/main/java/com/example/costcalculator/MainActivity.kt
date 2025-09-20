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
                    Greeting(
                        name = "Cost calculator",
                        modifier = Modifier.padding(innerPadding)
                    )
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CostCalculatorTheme {
        Greeting("Android")
    }
}