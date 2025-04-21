// RecipeAdapter.kt
package com.example.mealmate.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealmate.R
import com.example.mealmate.data.local.entity.Recipe
import com.example.mealmate.databinding.ItemRecipeBinding
import java.io.File

class RecipeAdapter(
    private val onItemClick: (Recipe) -> Unit,
    private val onSaveClick: ((Recipe) -> Unit)? = null
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnSave.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && onSaveClick != null) {
                    onSaveClick.invoke(getItem(position))
                }
            }
        }

        fun bind(recipe: Recipe) {
            binding.tvRecipeName.text = recipe.name

            // Show first 50 chars of ingredients as preview
            val ingredientPreview = if (recipe.ingredients.length > 50) {
                recipe.ingredients.substring(0, 50) + "..."
            } else {
                recipe.ingredients
            }
            binding.tvIngredients.text = ingredientPreview

            // Load image if available
            if (recipe.imageUri.isNotEmpty()) {
                binding.ivRecipeImage.visibility = View.VISIBLE

                // Check if it's a local file path or a URI
                val imageFile = File(recipe.imageUri)
                if (imageFile.exists()) {
                    Glide.with(binding.root.context)
                        .load(imageFile)
                        .centerCrop()
                        .placeholder(R.drawable.ic_placeholder_recipe)
                        .into(binding.ivRecipeImage)
                } else {
                    Glide.with(binding.root.context)
                        .load(recipe.imageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_placeholder_recipe)
                        .into(binding.ivRecipeImage)
                }
            } else {
                binding.ivRecipeImage.visibility = View.GONE
            }

            // Show/hide save button based on callback
            binding.btnSave.visibility = if (onSaveClick != null) View.VISIBLE else View.GONE
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}