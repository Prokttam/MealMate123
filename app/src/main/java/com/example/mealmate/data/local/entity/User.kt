// User.kt
package com.example.mealmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val profileImage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
