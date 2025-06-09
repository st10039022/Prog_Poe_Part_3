package com.example.ssbbudgettracker.model

import java.util.Date

data class Expense(
    val id: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Date? = null,
    val photoUri: String = "",
    val userId: String = ""
)
