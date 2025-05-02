package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// this defines the expenses table in the database
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique id for each expense
    val category: String, // name of the category for this expense
    val amount: Double, // amount of money spent
    val description: String, // short description of the expense
    val date: String, // date the expense was added
    val photoUri: String? = null, // optional image path for a receipt or photo
    val userId: Int // links the expense to the logged-in user
)
