package com.example.mealmate.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.SessionManager
import com.example.mealmate.data.local.MealMateDatabase
import com.example.mealmate.data.local.entity.Recipe
import com.example.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    // Create instances directly in the ViewModel
    private val database = MealMateDatabase.getDatabase(application)
    private val repository = RecipeRepository(database.recipeDao())
    private val sessionManager = SessionManager(application)

    val allRecipes: LiveData<List<Recipe>> = repository.getAllRecipes(sessionManager.getUserId()).asLiveData()
    val publicRecipes: LiveData<List<Recipe>> = repository.getAllPublicRecipes().asLiveData()

    fun getRecipeById(id: Int): LiveData<Recipe?> {
        return repository.getRecipeById(id).asLiveData()
    }

    fun insert(recipe: Recipe) = viewModelScope.launch {
        // Ensure recipe has current user ID
        val recipeWithUserId = recipe.copy(userId = sessionManager.getUserId())
        repository.insert(recipeWithUserId)
    }

    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.update(recipe)
    }

    fun delete(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }

    fun shareRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.shareRecipe(recipe)
    }

    suspend fun getRecipeByIdSuspend(id: Int): Recipe? {
        return repository.getRecipeByIdSuspend(id)
    }
}