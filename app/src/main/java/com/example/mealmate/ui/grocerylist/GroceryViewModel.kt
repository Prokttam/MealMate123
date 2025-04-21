package com.example.mealmate.ui.grocerylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.SessionManager
import com.example.mealmate.data.local.MealMateDatabase
import com.example.mealmate.data.local.entity.GroceryItem
import com.example.mealmate.data.local.entity.Recipe
import com.example.mealmate.data.repository.GroceryRepository
import com.example.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class GroceryViewModel(application: Application) : AndroidViewModel(application) {
    // Create instances directly in the ViewModel instead of accessing through MealMateApplication
    private val database = MealMateDatabase.getDatabase(application)
    private val repository = GroceryRepository(database.groceryItemDao())
    private val recipeRepository = RecipeRepository(database.recipeDao())
    private val sessionManager = SessionManager(application)

    val allGroceryItems: LiveData<List<GroceryItem>> =
        repository.getAllGroceryItems(sessionManager.getUserId()).asLiveData()

    val unpurchasedItems: LiveData<List<GroceryItem>> =
        repository.getUnpurchasedItems(sessionManager.getUserId()).asLiveData()

    fun insert(groceryItem: GroceryItem) = viewModelScope.launch {
        // Ensure item has current user ID
        val itemWithUserId = groceryItem.copy(userId = sessionManager.getUserId())
        repository.insert(itemWithUserId)
    }

    fun insertAll(groceryItems: List<GroceryItem>) = viewModelScope.launch {
        // Ensure items have current user ID
        val itemsWithUserId = groceryItems.map {
            it.copy(userId = sessionManager.getUserId())
        }
        repository.insertAll(itemsWithUserId)
    }

    fun update(groceryItem: GroceryItem) = viewModelScope.launch {
        repository.update(groceryItem)
    }

    fun delete(groceryItem: GroceryItem) = viewModelScope.launch {
        repository.delete(groceryItem)
    }

    fun togglePurchased(groceryItem: GroceryItem) = viewModelScope.launch {
        val updatedItem = groceryItem.copy(isPurchased = !groceryItem.isPurchased)
        repository.update(updatedItem)
    }

    fun deletePurchasedItems() = viewModelScope.launch {
        repository.deletePurchasedItems(sessionManager.getUserId())
    }

    fun addRecipeIngredientsToGroceryList(recipeId: Int) = viewModelScope.launch {
        val recipe = recipeRepository.getRecipeByIdSuspend(recipeId) ?: return@launch

        // Parse ingredients and add to grocery list
        val ingredients = parseIngredientsFromRecipe(recipe)
        insertAll(ingredients)
    }

    private fun parseIngredientsFromRecipe(recipe: Recipe): List<GroceryItem> {
        // This is a simple implementation
        // In a real app, you'd use a more sophisticated parsing method

        return recipe.ingredients.split("\n").map { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) {
                return@map null
            }

            // Try to extract quantity and unit
            val regex = Regex("""^([\d.]+)\s*([a-zA-Z]+)?\s+(.+)$""")
            val match = regex.find(trimmedLine)

            if (match != null) {
                val (quantityStr, unit, name) = match.destructured
                val quantity = quantityStr.toFloatOrNull() ?: 1f

                GroceryItem(
                    userId = sessionManager.getUserId(),
                    name = name.trim(),
                    category = determineCategory(name.trim()),
                    quantity = quantity,
                    unit = unit.trim(),
                    recipeId = recipe.id
                )
            } else {
                // If no pattern match, just use the whole line as the name
                GroceryItem(
                    userId = sessionManager.getUserId(),
                    name = trimmedLine,
                    category = determineCategory(trimmedLine),
                    recipeId = recipe.id
                )
            }
        }.filterNotNull()
    }

    private fun determineCategory(ingredient: String): String {
        val lowerIngredient = ingredient.lowercase()

        return when {
            // Check for categories
            lowerIngredient.contains("apple") ||
                    lowerIngredient.contains("banana") ||
                    lowerIngredient.contains("berry") ||
                    lowerIngredient.contains("fruit") ||
                    lowerIngredient.contains("vegetable") ||
                    lowerIngredient.contains("carrot") ||
                    lowerIngredient.contains("onion") ||
                    lowerIngredient.contains("potato") -> "Fruits & Vegetables"

            lowerIngredient.contains("chicken") ||
                    lowerIngredient.contains("beef") ||
                    lowerIngredient.contains("pork") ||
                    lowerIngredient.contains("fish") ||
                    lowerIngredient.contains("seafood") ||
                    lowerIngredient.contains("meat") -> "Meat & Seafood"

            lowerIngredient.contains("milk") ||
                    lowerIngredient.contains("cheese") ||
                    lowerIngredient.contains("yogurt") ||
                    lowerIngredient.contains("cream") ||
                    lowerIngredient.contains("butter") ||
                    lowerIngredient.contains("egg") -> "Dairy & Eggs"

            lowerIngredient.contains("bread") ||
                    lowerIngredient.contains("bun") ||
                    lowerIngredient.contains("bagel") ||
                    lowerIngredient.contains("roll") -> "Bakery"

            lowerIngredient.contains("flour") ||
                    lowerIngredient.contains("sugar") ||
                    lowerIngredient.contains("oil") ||
                    lowerIngredient.contains("pasta") ||
                    lowerIngredient.contains("rice") ||
                    lowerIngredient.contains("bean") ||
                    lowerIngredient.contains("sauce") ||
                    lowerIngredient.contains("spice") -> "Pantry"

            else -> "Other"
        }
    }
}