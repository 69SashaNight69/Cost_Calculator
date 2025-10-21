package com.example.costcalculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.costcalculator.data.AppDatabase
import com.example.costcalculator.data.Category
import com.example.costcalculator.data.CategoryDao
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.ExpenseDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseDao: ExpenseDao
    private val categoryDao: CategoryDao

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        expenseDao = database.expenseDao()
        categoryDao = database.categoryDao()
    }

    // Тепер дані беруться напряму з бази даних і є StateFlow
    val expenses: StateFlow<List<Expense>> = expenseDao.getAllExpenses()
        .combine(_selectedCategoryId) { allExpenses, categoryId ->
            if (categoryId == null) {
                allExpenses // Якщо категорія не обрана, показуємо все
            } else {
                // Знаходимо назву категорії за її ID
                val categoryName = categories.value.find { it.id == categoryId }?.name
                // Фільтруємо витрати за цією назвою
                allExpenses.filter { it.category == categoryName }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<Category>> = categoryDao.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Функція для додавання нової витрати
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insert(expense)
        }
    }


    // Функція для видалення витрати
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.delete(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.update(expense)
        }
    }

    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            categoryDao.insert(Category(name = categoryName))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.delete(category)
        }
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
    }
}