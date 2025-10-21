package com.example.costcalculator.data.repository

import com.example.costcalculator.data.Category
import com.example.costcalculator.data.CategoryDao
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.ExpenseDao
import kotlinx.coroutines.flow.Flow

// Репозиторій приймає DAO як параметри
class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {
    // --- Операції з Витратами ---

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense)
    }

    // --- Операції з Категоріями ---

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }
}