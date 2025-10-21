package com.example.costcalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.ExpenseGroup
import com.example.costcalculator.utils.toCurrencyFormat

@Composable
fun ExpenseItem(
    expense: Expense,
    group: ExpenseGroup?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = expense.category,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (group != null) {
            Text(
                text = "Група: ${group.name}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        expense.description?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = expense.amount.toCurrencyFormat(),
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class) // ДОДАНО: анотація для SwipeToDismissBox
@Composable
fun ExpenseList(
    expenses: List<Expense>,
    groups: List<ExpenseGroup>,
    modifier: Modifier = Modifier,
    onExpenseClick: (Long) -> Unit,
    onExpenseSwiped: (Expense) -> Unit // ДОДАНО: параметр для свайпу
) {
    LazyColumn(modifier = modifier) {
        items(
            items = expenses,
            key = { it.id } // Ключ для правильної анімації
        ) { expense ->
            // ДОДАНО: обгортка для свайпу
            val group = groups.find { it.id == expense.groupId }
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) { // Свайп зправа наліво
                        onExpenseSwiped(expense)
                        true // Дозволити видалення
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false, // Вимкнути свайп зліва направо
                backgroundContent = {
                }
            ) {
                // Наш старий добрий ExpenseItem
                ExpenseItem(
                    expense = expense,
                    group = group,
                    onClick = { onExpenseClick(expense.id) }
                )
            }
        }
    }
}