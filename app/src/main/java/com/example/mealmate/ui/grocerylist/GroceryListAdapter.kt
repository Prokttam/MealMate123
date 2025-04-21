// GroceryListAdapter.kt
package com.example.mealmate.ui.grocerylist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.data.local.entity.GroceryItem
import com.example.mealmate.databinding.ItemGroceryBinding

class GroceryListAdapter :
    ListAdapter<GroceryItem, GroceryListAdapter.GroceryViewHolder>(GroceryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val binding = ItemGroceryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroceryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroceryViewHolder(private val binding: ItemGroceryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroceryItem) {
            binding.tvItemName.text = item.name
            binding.tvCategory.text = item.category

            val quantityText = if (item.quantity > 0 && item.unit.isNotEmpty()) {
                "${item.quantity} ${item.unit}"
            } else if (item.quantity > 0) {
                item.quantity.toString()
            } else {
                ""
            }
            binding.tvQuantity.text = quantityText

            // Apply strikethrough on purchased items
            if (item.isPurchased) {
                binding.tvItemName.paintFlags = binding.tvItemName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvQuantity.paintFlags = binding.tvQuantity.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.checkbox.isChecked = true
            } else {
                binding.tvItemName.paintFlags = binding.tvItemName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvQuantity.paintFlags = binding.tvQuantity.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.checkbox.isChecked = false
            }

            // Handle checkbox changes
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != item.isPurchased) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val currentItem = getItem(position)
                        val updatedItem = currentItem.copy(isPurchased = isChecked)
                        notifyItemChanged(position)
                        // Use a callback to update the database
                    }
                }
            }
        }
    }

    class GroceryDiffCallback : DiffUtil.ItemCallback<GroceryItem>() {
        override fun areItemsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
            return oldItem == newItem
        }
    }
}