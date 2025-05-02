package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: Int = 1,
    val minAmount: Double,
    val maxAmount: Double
)
