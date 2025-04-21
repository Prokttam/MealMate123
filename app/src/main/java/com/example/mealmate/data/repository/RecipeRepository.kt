package com.example.mealmate.data.repository

import com.example.mealmate.data.local.dao.RecipeDao
import com.example.mealmate.data.local.entity.Recipe
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {

    fun getAllRecipes(userId: Int): Flow<List<Recipe>> =
        recipeDao.getAllRecipes(userId)

    fun getAllPublicRecipes(): Flow<List<Recipe>> =
        recipeDao.getAllPublicRecipes()

    fun getRecipeById(id: Int): Flow<Recipe?> =
        recipeDao.getRecipeById(id)

    suspend fun getRecipeByIdSuspend(id: Int): Recipe? =
        recipeDao.getRecipeByIdSuspend(id)

    suspend fun insert(recipe: Recipe): Long =
        recipeDao.insert(recipe)

    suspend fun update(recipe: Recipe) =
        recipeDao.update(recipe)

    suspend fun delete(recipe: Recipe) =
        recipeDao.delete(recipe)

    suspend fun shareRecipe(recipe: Recipe) {
        val sharedRecipe = recipe.copy(id = 0, isPublic = true)
        recipeDao.insert(sharedRecipe)
    }
}