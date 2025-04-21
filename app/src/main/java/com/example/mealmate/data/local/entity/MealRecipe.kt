package com.example.mealmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_recipes",
    foreignKeys = [
        ForeignKey(
            entity = MealPlan::class,
            parentColumns = ["id"],
            childColumns = ["mealPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealPlanId"), Index("recipeId")]
)
data class MealRecipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var mealPlanId: Int,
    var recipeId: Int
)