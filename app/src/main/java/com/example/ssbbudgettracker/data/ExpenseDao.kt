package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// this interface handles all expense database actions
@Dao
interface ExpenseDao {

    // inserts a new expense into the database
    @Insert
    suspend fun insertExpense(expense: ExpenseEntity)

    // gets all expenses from the table
    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<ExpenseEntity>
}
