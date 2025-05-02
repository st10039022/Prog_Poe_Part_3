package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// this defines the income_categories table in the database
@Entity(tableName = "income_categories")
data class IncomeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique id for each income category
    val name: String // name of the income category (like salary, freelance)
)
