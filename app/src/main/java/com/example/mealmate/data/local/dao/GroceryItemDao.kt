package com.example.mealmate.data.local.dao

import androidx.room.*
import com.example.mealmate.data.local.entity.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {
    @Query("SELECT * FROM grocery_items WHERE userId = :userId ORDER BY category, name")
    fun getAllGroceryItems(userId: Int): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE userId = :userId AND isPurchased = 0")
    fun getUnpurchasedItems(userId: Int): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groceryItem: GroceryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groceryItems: List<GroceryItem>)

    @Update
    suspend fun update(groceryItem: GroceryItem)

    @Delete
    suspend fun delete(groceryItem: GroceryItem)

    @Query("DELETE FROM grocery_items WHERE userId = :userId AND isPurchased = 1")
    suspend fun deletePurchasedItems(userId: Int)

    @Query("SELECT * FROM grocery_items WHERE id = :id LIMIT 1")
    suspend fun getGroceryItemById(id: Int): GroceryItem?
}
