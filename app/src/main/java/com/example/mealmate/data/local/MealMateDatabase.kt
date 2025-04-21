package com.example.mealmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mealmate.data.local.dao.*
import com.example.mealmate.data.local.entity.*

@Database(
    entities = [
        User::class,
        Recipe::class,
        GroceryItem::class,
        MealPlan::class,
        MealRecipe::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MealMateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: MealMateDatabase? = null

        fun getDatabase(context: Context): MealMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealMateDatabase::class.java,
                    "mealmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}