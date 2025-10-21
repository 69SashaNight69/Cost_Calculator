package com.example.costcalculator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseGroupDao {
    @Insert
    suspend fun insert(group: ExpenseGroup)

    @Delete
    suspend fun delete(group: ExpenseGroup)

    @Query("SELECT * FROM expense_groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<ExpenseGroup>>
}