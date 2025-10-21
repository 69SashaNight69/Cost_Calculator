package com.example.costcalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costcalculator.data.Category
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ЗМІНА 1: Конструктор тепер приймає репозиторій
class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // ЗМІНА 2: Ми прибрали всю логіку з DAO та init {}

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    // Дані тепер надходять з репозиторію
    val expenses: StateFlow<List<Expense>> = repository.getAllExpenses()
        .combine(_selectedCategoryId) { allExpenses, categoryId ->
            if (categoryId == null) {
                allExpenses
            } else {
                // Трохи покращимо логіку фільтрації, щоб уникнути зайвих запитів
                val categoryName = repository.getAllCategories().first().find { it.id == categoryId }?.name
                allExpenses.filter { it.category == categoryName }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ЗМІНА 3: Всі функції тепер просто викликають методи репозиторію
    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return repository.getExpenseById(id)
    }

    fun addCategory(categoryName: String) = viewModelScope.launch {
        repository.insertCategory(Category(name = categoryName))
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }
}