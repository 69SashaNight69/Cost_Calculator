package com.example.costcalculator.utils

fun Double.toCurrencyFormat(): String {
    return "%.2f грн".format(this)
}