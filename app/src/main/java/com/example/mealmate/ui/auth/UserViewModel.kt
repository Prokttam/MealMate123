package com.example.mealmate.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.SessionManager
import com.example.mealmate.data.local.MealMateDatabase
import com.example.mealmate.data.local.entity.User
import com.example.mealmate.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    // Create instances directly in the ViewModel instead of accessing through MealMateApplication
    private val database = MealMateDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao())
    private val sessionManager = SessionManager(application)

    val currentUser: LiveData<User?> = repository.currentUser

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    init {
        // Check if there's a logged-in user
        viewModelScope.launch {
            if (sessionManager.isLoggedIn()) {
                val userId = sessionManager.getUserId()
                val user = repository.getUserById(userId)
                if (user != null) {
                    _loginState.value = LoginState.LoggedIn(user)
                } else {
                    _loginState.value = LoginState.Error("Session expired")
                    sessionManager.clearSession()
                }
            } else {
                _loginState.value = LoginState.NotLoggedIn
            }
        }
    }

    suspend fun registerUser(name: String, email: String, password: String): Result<User> {
        _loginState.value = LoginState.Loading

        return repository.registerUser(name, email, password).also { result ->
            result.fold(
                onSuccess = {
                    sessionManager.saveUserSession(it)
                    _loginState.value = LoginState.LoggedIn(it)
                },
                onFailure = {
                    _loginState.value = LoginState.Error(it.message ?: "Registration failed")
                }
            )
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        _loginState.value = LoginState.Loading

        return repository.loginUser(email, password).also { result ->
            result.fold(
                onSuccess = {
                    sessionManager.saveUserSession(it)
                    _loginState.value = LoginState.LoggedIn(it)
                },
                onFailure = {
                    _loginState.value = LoginState.Error(it.message ?: "Login failed")
                }
            )
        }
    }

    fun logoutUser() {
        repository.logoutUser()
        sessionManager.clearSession()
        _loginState.value = LoginState.NotLoggedIn
    }

    // New method to update current user
    fun updateCurrentUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    sealed class LoginState {
        object NotLoggedIn : LoginState()
        object Loading : LoginState()
        data class LoggedIn(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}