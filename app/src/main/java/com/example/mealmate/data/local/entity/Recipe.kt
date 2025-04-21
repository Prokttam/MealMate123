
// Recipe.kt
package com.example.mealmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipes",
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
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var userId: Int,
    var name: String,
    var ingredients: String,
    var instructions: String,
    var imageUri: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var isPublic: Boolean = false
)