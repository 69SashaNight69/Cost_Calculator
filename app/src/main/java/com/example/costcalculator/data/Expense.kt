package com.example.costcalculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val description: String?,
    val groupId: Long?,
    val latitude: Double?,
    val longitude: Double?
)