package com.example.ssbbudgettracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// this interface handles all user database actions
@Dao
interface UserDao {

    // inserts a new user into the database
    @Insert
    suspend fun registerUser(user: UserEntity)

    // checks if a user with the given username and password exists
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun loginUser(username: String, password: String): UserEntity?
}
