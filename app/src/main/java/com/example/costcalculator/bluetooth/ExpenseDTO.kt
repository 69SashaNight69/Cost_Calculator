package com.example.costcalculator.bluetooth

data class ExpenseDTO(
    val amount: Double,
    val category: String,
    val description: String?,
    val groupName: String?,
    val latitude: Double?,
    val longitude: Double?
)