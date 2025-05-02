package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// this interface handles all income database actions
@Dao
interface IncomeDao {

    // inserts a new income into the database
    @Insert
    suspend fun insertIncome(income: IncomeEntity)

    // gets all income records from the table
    @Query("SELECT * FROM incomes")
    suspend fun getAllIncomes(): List<IncomeEntity>
}
