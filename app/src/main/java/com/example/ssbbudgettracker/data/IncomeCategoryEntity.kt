package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_categories")
data class IncomeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
