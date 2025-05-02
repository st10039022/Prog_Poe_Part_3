package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setGoals(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE id = 1")
    suspend fun getGoals(): GoalEntity?
}
