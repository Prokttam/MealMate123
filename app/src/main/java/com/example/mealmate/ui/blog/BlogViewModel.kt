package com.example.mealmate.ui.blog

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

class BlogViewModel(application: Application) : AndroidViewModel(application) {
    // Create instances directly in the ViewModel
    private val database = MealMateDatabase.getDatabase(application)
    private val repository = RecipeRepository(database.recipeDao())
    private val sessionManager = SessionManager(application)

    val publicRecipes: LiveData<List<Recipe>> = repository.getAllPublicRecipes().asLiveData()

    fun saveRecipeToMyCollection(recipe: Recipe) = viewModelScope.launch {
        val newRecipe = recipe.copy(
            id = 0,
            userId = sessionManager.getUserId(),
            isPublic = false
        )
        repository.insert(newRecipe)
    }
}