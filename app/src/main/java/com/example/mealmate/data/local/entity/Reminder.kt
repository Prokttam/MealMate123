package com.example.mealmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("recipeId")]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var userId: Int,
    var title: String,
    var message: String,
    var reminderTime: Long,
    var recipeId: Int? = null, // Optional, can be null if not associated with a recipe
    var isCompleted: Boolean = false
)