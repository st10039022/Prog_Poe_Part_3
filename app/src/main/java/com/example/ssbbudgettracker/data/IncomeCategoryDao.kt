package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncomeCategoryDao {
    @Insert
    suspend fun insertCategory(category: IncomeCategoryEntity)

    @Query("SELECT * FROM income_categories")
    suspend fun getAllCategories(): List<IncomeCategoryEntity>
}
