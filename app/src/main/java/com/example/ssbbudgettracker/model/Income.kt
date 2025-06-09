package com.example.ssbbudgettracker.model

data class Income(
    var id: String = "",
    var category: String = "",
    var amount: Double = 0.0,
    var description: String = "",
    var date: String = ""
)
