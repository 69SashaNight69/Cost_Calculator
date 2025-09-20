package com.example.costcalculator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.costcalculator.data.Expense
import com.example.costcalculator.utils.toCurrencyFormat
import androidx.compose.foundation.clickable


@Composable
fun ExpenseItem(
    expense: Expense,
    modifier: Modifier = Modifier,
    onClick: () -> Unit // Додали параметр
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Робимо Column клікабельним
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
fun ExpenseList(
    expenses: List<Expense>,
    modifier: Modifier = Modifier,
    onExpenseClick: (Long) -> Unit // Приймаємо лямбду
) {
    LazyColumn(modifier = modifier) {
        items(expenses) { expense ->
            ExpenseItem(
                expense = expense,
                onClick = { onExpenseClick(expense.id) } // Передаємо ID
            )
        }
    }
}