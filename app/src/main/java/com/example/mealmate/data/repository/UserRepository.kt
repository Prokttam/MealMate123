package com.example.mealmate.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mealmate.data.local.dao.UserDao
import com.example.mealmate.data.local.entity.User
import com.example.mealmate.util.SecurityUtil

class UserRepository(private val userDao: UserDao) {
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    suspend fun registerUser(name: String, email: String, password: String): Result<User> {
        return try {
            // Check if user already exists
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception("User with this email already exists"))
            } else {
                // Create new user
                val passwordHash = SecurityUtil.hashPassword(password)
                val user = User(name = name, email = email, passwordHash = passwordHash)
                val userId = userDao.insert(user)
                val createdUser = userDao.getUserById(userId.toInt())

                if (createdUser != null) {
                    _currentUser.postValue(createdUser)
                    Result.success(createdUser)
                } else {
                    Result.failure(Exception("Failed to create user"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)

            if (user != null && SecurityUtil.checkPassword(password, user.passwordHash)) {
                _currentUser.postValue(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    fun logoutUser() {
        _currentUser.postValue(null)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
        _currentUser.postValue(user)
    }
}
