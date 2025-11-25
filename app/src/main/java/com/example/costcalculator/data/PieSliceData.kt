package com.example.costcalculator.data

import androidx.compose.ui.graphics.Color

data class PieSliceData(
    val value: Float,
    val label: String,
    val color: Color,
    val sweepAngle: Float
)