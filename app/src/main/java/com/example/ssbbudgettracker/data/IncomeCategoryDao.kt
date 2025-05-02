package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// this interface handles all income category database actions
@Dao
interface IncomeCategoryDao {

    // inserts a new income category into the database
    @Insert
    suspend fun insertCategory(category: IncomeCategoryEntity)

    // gets all income categories from the table
    @Query("SELECT * FROM income_categories")
    suspend fun getAllCategories(): List<IncomeCategoryEntity>
}
