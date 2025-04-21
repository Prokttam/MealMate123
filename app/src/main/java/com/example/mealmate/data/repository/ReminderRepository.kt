package com.example.mealmate.data.repository

import com.example.mealmate.data.local.dao.ReminderDao
import com.example.mealmate.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    fun getUpcomingReminders(userId: Int, currentTime: Long): Flow<List<Reminder>> =
        reminderDao.getUpcomingReminders(userId, currentTime)

    fun getPastReminders(userId: Int, currentTime: Long): Flow<List<Reminder>> =
        reminderDao.getPastReminders(userId, currentTime)

    suspend fun insert(reminder: Reminder): Long =
        reminderDao.insert(reminder)

    suspend fun update(reminder: Reminder) =
        reminderDao.update(reminder)

    suspend fun delete(reminder: Reminder) =
        reminderDao.delete(reminder)

    suspend fun deleteCompletedReminders(userId: Int) =
        reminderDao.deleteCompletedReminders(userId)

    suspend fun getReminderById(id: Int): Reminder? =
        reminderDao.getReminderById(id)
}