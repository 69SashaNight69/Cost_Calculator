package com.example.costcalculator.data

data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val description: String?
)