package com.example.mealmate.data.local.dao

import androidx.room.*
import com.example.mealmate.data.local.entity.MealPlan
import com.example.mealmate.data.local.entity.MealRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans WHERE userId = :userId ORDER BY date ASC")
    fun getAllMealPlans(userId: Int): Flow<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getMealPlansForDateRange(userId: Int, startDate: Long, endDate: Long): Flow<List<MealPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealPlan: MealPlan): Long

    @Update
    suspend fun update(mealPlan: MealPlan)

    @Delete
    suspend fun delete(mealPlan: MealPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealRecipe(mealRecipe: MealRecipe): Long

    @Query("DELETE FROM meal_recipes WHERE mealPlanId = :mealPlanId")
    suspend fun deleteMealRecipesForMealPlan(mealPlanId: Int)

    @Transaction
    @Query("SELECT * FROM recipes WHERE id IN (SELECT recipeId FROM meal_recipes WHERE mealPlanId = :mealPlanId)")
    fun getRecipesForMealPlan(mealPlanId: Int): Flow<List<com.example.mealmate.data.local.entity.Recipe>>
}