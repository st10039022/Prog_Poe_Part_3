package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncomeDao {
    @Insert
    suspend fun insertIncome(income: IncomeEntity)

    @Query("SELECT * FROM incomes")
    suspend fun getAllIncomes(): List<IncomeEntity>
}
