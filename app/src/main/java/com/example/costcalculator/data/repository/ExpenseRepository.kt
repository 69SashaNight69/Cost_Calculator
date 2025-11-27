package com.example.costcalculator.data.repository

import com.example.costcalculator.data.Category
import com.example.costcalculator.data.CategoryDao
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.ExpenseDao
import com.example.costcalculator.data.ExpenseGroup
import com.example.costcalculator.data.ExpenseGroupDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// Репозиторій приймає DAO як параметри
class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val groupDao: ExpenseGroupDao
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

    // --- Операції з Групами ---
    fun getAllGroups(): Flow<List<ExpenseGroup>> = groupDao.getAllGroups()

    suspend fun insertGroup(group: ExpenseGroup) {
        groupDao.insert(group)
    }

    suspend fun deleteGroup(group: ExpenseGroup) {
        groupDao.delete(group)
    }
    suspend fun findOrCreateCategory(categoryName: String): Category {
        val existingCategory = categoryDao.getAllCategories().first()
            .find { it.name.equals(categoryName, ignoreCase = true) }

        return if (existingCategory != null) {
            existingCategory
        } else {
            val newCategory = Category(name = categoryName)
            categoryDao.insert(newCategory)
            // Робимо ще один запит, щоб отримати об'єкт з уже згенерованим ID
            categoryDao.getAllCategories().first().find { it.name.equals(categoryName, ignoreCase = true) }!!
        }
    }

    // Робить те ж саме для групи
    suspend fun findOrCreateGroup(groupName: String): ExpenseGroup {
        val existingGroup = groupDao.getAllGroups().first()
            .find { it.name.equals(groupName, ignoreCase = true) }

        return if (existingGroup != null) {
            existingGroup
        } else {
            val newGroup = ExpenseGroup(name = groupName, description = null)
            groupDao.insert(newGroup)
            groupDao.getAllGroups().first().find { it.name.equals(groupName, ignoreCase = true) }!!
        }
    }
}