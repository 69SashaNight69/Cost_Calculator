package com.example.costcalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.costcalculator.data.Expense

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExpenseViewModel : ViewModel() {

    // 1. Приватний, змінюваний StateFlow.
    // Тільки ViewModel може змінювати цей список.
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())

    // 2. Публічний, незмінюваний StateFlow.
    // UI може тільки читати дані з нього.
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    // 3. Блок init викликається при створенні ViewModel.
    // Тут ми завантажуємо наші початкові дані.
    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        // Поки що ми використовуємо ті ж самі тестові дані.
        // У майбутньому тут буде завантаження з бази даних.
        _expenses.value = listOf(
            Expense(1L, 75.50, "Продукти", "Хліб, молоко"),
            Expense(2L, 1200.00, "Оренда", "Платіж за квартиру"),
            Expense(3L, 40.00, "Транспорт", "Квиток на автобус"),
            Expense(4L, 350.25, "Розваги", "Квитки в кіно"),
            Expense(5L, 150.00, "Кафе", "Кава та десерт"),
            Expense(6L, 80.00, "Транспорт", "Поповнення проїзного"),
            Expense(7L, 500.00, "Одяг", "Нова футболка")
        )
    }

    fun getExpenseById(id: Long): Expense? {
        // Ми шукаємо у поточному списку витрат ту,
        // у якої id збігається з потрібним.
        return expenses.value.find { it.id == id }
    }
}