package com.example.mealmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_plans",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class MealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var userId: Int,
    var dayOfWeek: String,
    var date: Long,
    var mealType: String // breakfast, lunch, dinner, etc.
)
