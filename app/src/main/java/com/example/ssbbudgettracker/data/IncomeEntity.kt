package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// this defines the incomes table in the database
@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique id for each income entry
    val category: String, // name of the income category
    val amount: Double, // amount of income received
    val description: String, // short description of the income
    val date: String // date the income was added
)
