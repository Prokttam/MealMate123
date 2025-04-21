package com.example.mealmate.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "grocery_items",
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
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var userId: Int,
    var name: String,
    var category: String,
    var quantity: Float = 0f,
    var unit: String = "",
    var isPurchased: Boolean = false,
    var recipeId: Int = 0 // Optional, can be 0 if added manually
) : Parcelable