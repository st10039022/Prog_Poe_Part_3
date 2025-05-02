package com.example.ssbbudgettracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ExpenseEntity::class,
        GoalEntity::class,
        IncomeEntity::class,
        IncomeCategoryEntity::class
    ],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    // these provide access to each dao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun goalDao(): GoalDao
    abstract fun incomeDao(): IncomeDao
    abstract fun incomeCategoryDao(): IncomeCategoryDao

    companion object {
        // holds the single instance of the database
        @Volatile private var INSTANCE: AppDatabase? = null

        // this creates or returns the database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ssb_database"
                ).fallbackToDestructiveMigration().build()// clears and resets db on version change
                INSTANCE = instance
                return instance
            }
        }
    }
}
