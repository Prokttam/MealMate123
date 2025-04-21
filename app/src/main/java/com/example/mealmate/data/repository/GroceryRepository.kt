package com.example.mealmate.data.repository

import com.example.mealmate.data.local.dao.GroceryItemDao
import com.example.mealmate.data.local.entity.GroceryItem
import kotlinx.coroutines.flow.Flow

class GroceryRepository(private val groceryItemDao: GroceryItemDao) {

    fun getAllGroceryItems(userId: Int): Flow<List<GroceryItem>> =
        groceryItemDao.getAllGroceryItems(userId)

    fun getUnpurchasedItems(userId: Int): Flow<List<GroceryItem>> =
        groceryItemDao.getUnpurchasedItems(userId)

    suspend fun insert(groceryItem: GroceryItem): Long =
        groceryItemDao.insert(groceryItem)

    suspend fun insertAll(groceryItems: List<GroceryItem>) =
        groceryItemDao.insertAll(groceryItems)

    suspend fun update(groceryItem: GroceryItem) =
        groceryItemDao.update(groceryItem)

    suspend fun delete(groceryItem: GroceryItem) =
        groceryItemDao.delete(groceryItem)

    suspend fun deletePurchasedItems(userId: Int) =
        groceryItemDao.deletePurchasedItems(userId)

    suspend fun getGroceryItemById(id: Int): GroceryItem? =
        groceryItemDao.getGroceryItemById(id)
}