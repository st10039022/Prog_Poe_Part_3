package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// this interface handles all category database actions
@Dao
interface CategoryDao {

    // inserts a new category into the database
    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    // gets all categories from the table
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    // gets only categories of a certain type (like expense or income)
    @Query("SELECT * FROM categories WHERE type = :type")
    suspend fun getCategoriesByType(type: String): List<CategoryEntity>
}
