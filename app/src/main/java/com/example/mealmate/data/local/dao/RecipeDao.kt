package com.example.mealmate.data.local.dao

import androidx.room.*
import com.example.mealmate.data.local.entity.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllRecipes(userId: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun getAllPublicRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Flow<Recipe?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe): Long

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeByIdSuspend(id: Int): Recipe?
}