package com.example.mealmate.data.local.dao

import androidx.room.*
import com.example.mealmate.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId AND reminderTime >= :currentTime ORDER BY reminderTime ASC")
    fun getUpcomingReminders(userId: Int, currentTime: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND reminderTime < :currentTime ORDER BY reminderTime DESC")
    fun getPastReminders(userId: Int, currentTime: Long): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE userId = :userId AND isCompleted = 1")
    suspend fun deleteCompletedReminders(userId: Int)

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Int): Reminder?
}