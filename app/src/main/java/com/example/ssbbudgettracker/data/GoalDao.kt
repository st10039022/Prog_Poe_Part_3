package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// this interface handles all goal database actions
@Dao
interface GoalDao {

    // inserts a goal into the database and replaces it if one already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    // gets the goal with id = 1 (only one goal is expected to exist)
    @Query("SELECT * FROM goals WHERE id = 1 LIMIT 1")
    suspend fun getGoals(): GoalEntity?
}
