package com.example.ssbbudgettracker.model

data class Goal(
    var id: String = "",
    var minAmount: Double = 0.0,
    var maxAmount: Double = 0.0,
    var month: String = "" // e.g. "2025-06"
)
