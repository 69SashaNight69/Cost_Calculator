package com.example.costcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.costcalculator.ui.AppNavigation
import com.example.costcalculator.ui.theme.CostCalculatorTheme
import com.example.costcalculator.ui.screens.ExpenseTrackerScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CostCalculatorTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseTrackerScreenPreview() {
    CostCalculatorTheme {
        ExpenseTrackerScreen(onExpenseClick = { })
    }
}