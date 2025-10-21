package com.example.costcalculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_groups")
data class ExpenseGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String?
)