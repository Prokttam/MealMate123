package com.example.mealmate.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.SessionManager
import com.example.mealmate.data.local.MealMateDatabase
import com.example.mealmate.data.local.entity.MealPlan
import com.example.mealmate.data.local.entity.Recipe
import com.example.mealmate.data.repository.RecipeRepository
import com.example.mealmate.util.DateTimeUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // Create instances directly in the ViewModel
    private val database = MealMateDatabase.getDatabase(application)
    private val recipeRepository = RecipeRepository(database.recipeDao())
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    val recentRecipes: LiveData<List<Recipe>> = recipeRepository.getAllRecipes(userId).asLiveData()

    private val _weekStartDate = MutableLiveData<Long>()
    val weekStartDate: LiveData<Long> = _weekStartDate

    init {
        // Initialize with current week
        setCurrentWeek()
    }

    fun setCurrentWeek() {
        val currentTime = System.currentTimeMillis()
        _weekStartDate.value = DateTimeUtils.getStartOfWeek(currentTime)
    }

    fun moveWeekForward() {
        _weekStartDate.value?.let { currentStart ->
            val calendar = Calendar.getInstance().apply { timeInMillis = currentStart }
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            _weekStartDate.value = calendar.timeInMillis
        }
    }

    fun moveWeekBackward() {
        _weekStartDate.value?.let { currentStart ->
            val calendar = Calendar.getInstance().apply { timeInMillis = currentStart }
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            _weekStartDate.value = calendar.timeInMillis
        }
    }

    fun getWeekDays(): List<Long> {
        val startDate = _weekStartDate.value ?: DateTimeUtils.getStartOfWeek(System.currentTimeMillis())
        return List(7) { index ->
            val calendar = Calendar.getInstance().apply { timeInMillis = startDate }
            calendar.add(Calendar.DAY_OF_YEAR, index)
            calendar.timeInMillis
        }
    }
}