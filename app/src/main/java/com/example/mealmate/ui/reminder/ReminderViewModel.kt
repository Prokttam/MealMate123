package com.example.mealmate.ui.reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.SessionManager
import com.example.mealmate.data.local.MealMateDatabase
import com.example.mealmate.data.local.entity.Reminder
import com.example.mealmate.data.repository.ReminderRepository
import com.example.mealmate.util.ReminderHelper
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    // Create instances directly in the ViewModel
    private val database = MealMateDatabase.getDatabase(application)
    private val repository = ReminderRepository(database.reminderDao())
    private val sessionManager = SessionManager(application)

    val currentTime = System.currentTimeMillis()
    val userId = sessionManager.getUserId()

    val upcomingReminders: LiveData<List<Reminder>> =
        repository.getUpcomingReminders(userId, currentTime).asLiveData()

    val pastReminders: LiveData<List<Reminder>> =
        repository.getPastReminders(userId, currentTime).asLiveData()

    fun scheduleReminder(reminder: Reminder) = viewModelScope.launch {
        // Ensure reminder has current user ID
        val reminderWithUserId = reminder.copy(userId = userId)
        val reminderId = repository.insert(reminderWithUserId).toInt()

        // Schedule actual notification
        ReminderHelper.scheduleReminder(
            getApplication(),
            reminderId,
            reminder.title,
            reminder.message,
            reminder.reminderTime
        )
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)

        // Reschedule notification if the reminder is in the future
        if (reminder.reminderTime > System.currentTimeMillis()) {
            ReminderHelper.cancelReminder(getApplication(), reminder.id)
            ReminderHelper.scheduleReminder(
                getApplication(),
                reminder.id,
                reminder.title,
                reminder.message,
                reminder.reminderTime
            )
        }
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        repository.delete(reminder)
        ReminderHelper.cancelReminder(getApplication(), reminder.id)
    }

    fun deleteCompletedReminders() = viewModelScope.launch {
        repository.deleteCompletedReminders(userId)
    }

    fun markReminderAsCompleted(reminder: Reminder) = viewModelScope.launch {
        val updatedReminder = reminder.copy(isCompleted = true)
        repository.update(updatedReminder)
    }
}