package com.example.ssbbudgettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// this defines the users table in the database
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // unique id for each user
    val username: String, // username used to log in
    val password: String // password for the user account
)
