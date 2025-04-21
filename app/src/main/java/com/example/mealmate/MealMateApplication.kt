package com.example.mealmate

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.mealmate.data.SessionManager
import com.example.mealmate.data.local.MealMateDatabase
import com.example.mealmate.data.repository.GroceryRepository
import com.example.mealmate.data.repository.RecipeRepository
import com.example.mealmate.data.repository.ReminderRepository
import com.example.mealmate.data.repository.UserRepository
import com.example.mealmate.util.Constants

class MealMateApplication : Application() {
    // Database instance
    val database by lazy { MealMateDatabase.getDatabase(this) }

    // Repositories
    val userRepository by lazy { UserRepository(database.userDao()) }
    val recipeRepository by lazy { RecipeRepository(database.recipeDao()) }
    val groceryRepository by lazy { GroceryRepository(database.groceryItemDao()) }
    val reminderRepository by lazy { ReminderRepository(database.reminderDao()) }

    // Session manager to track logged in user
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)

        // Create notification channel for API 26+
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.NOTIFICATION_CHANNEL_NAME
            val descriptionText = "Notifications for meal preparation reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        // Instance for global access
        lateinit var instance: MealMateApplication
            private set
    }
}