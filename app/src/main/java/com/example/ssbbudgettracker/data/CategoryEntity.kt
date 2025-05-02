package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// this defines the category table in the database
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique id for each category
    val name: String, // name of the category
    val type: String  // can be Income or expense
)
