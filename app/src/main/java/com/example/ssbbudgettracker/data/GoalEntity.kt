package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// this defines the goals table in the database
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: Int = 1, // fixed id since only one goal is saved at a time
    val minAmount: Double, // minimum amount user plans to spend
    val maxAmount: Double  // maximum amount user wants to stay under
)
