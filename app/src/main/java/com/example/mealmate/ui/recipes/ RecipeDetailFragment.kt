package com.example.mealmate.ui.recipes

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog  // Add this import
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mealmate.R
import com.example.mealmate.databinding.FragmentRecipeDetailBinding
import com.example.mealmate.ui.grocerylist.GroceryViewModel
import com.example.mealmate.ui.reminder.AddReminderDialog
import java.io.File



class RecipeDetailFragment : Fragment() {
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var groceryViewModel: GroceryViewModel
    private val args: RecipeDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
        groceryViewModel = ViewModelProvider(this)[GroceryViewModel::class.java]

        // Load recipe details
        recipeViewModel.getRecipeById(args.recipeId).observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                binding.tvRecipeName.text = it.name
                binding.tvIngredients.text = it.ingredients
                binding.tvInstructions.text = it.instructions

                // Load image if available
                if (it.imageUri.isNotEmpty()) {
                    val imageFile = File(it.imageUri)
                    if (imageFile.exists()) {
                        Glide.with(requireContext())
                            .load(imageFile)
                            .centerCrop()
                            .into(binding.ivRecipeImage)
                    } else {
                        Glide.with(requireContext())
                            .load(it.imageUri)
                            .centerCrop()
                            .into(binding.ivRecipeImage)
                    }
                    binding.ivRecipeImage.visibility = View.VISIBLE
                } else {
                    binding.ivRecipeImage.visibility = View.GONE
                }
            }
        }

        // Setup buttons
        binding.btnAddToGrocery.setOnClickListener {
            groceryViewModel.addRecipeIngredientsToGroceryList(args.recipeId)
            Toast.makeText(
                requireContext(),
                "Ingredients added to grocery list",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnSetReminder.setOnClickListener {
            showAddReminderDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_recipe -> {
                val action = RecipeDetailFragmentDirections.actionRecipeDetailToEditRecipe(args.recipeId)
                findNavController().navigate(action)
                true
            }
            R.id.action_delete_recipe -> {
                showDeleteConfirmationDialog()
                true
            }
            R.id.action_share_recipe -> {
                shareRecipe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete this recipe?")
            .setPositiveButton("Delete") { _, _ ->
                deleteRecipe()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteRecipe() {
        recipeViewModel.getRecipeById(args.recipeId).observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                recipeViewModel.delete(it)
                Toast.makeText(
                    requireContext(),
                    "Recipe deleted",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun shareRecipe() {
        recipeViewModel.getRecipeById(args.recipeId).observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                if (!it.isPublic) {
                    recipeViewModel.shareRecipe(it)
                    Toast.makeText(
                        requireContext(),
                        "Recipe shared to community",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Recipe is already shared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showAddReminderDialog() {
        val dialog = AddReminderDialog(args.recipeId)
        dialog.show(parentFragmentManager, "AddReminderDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}