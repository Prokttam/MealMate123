// RecipeListFragment.kt
package com.example.mealmate.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Setup adapter
        adapter = RecipeAdapter(
            onItemClick = { recipe ->
                val action = RecipeListFragmentDirections.actionRecipesToRecipeDetail(recipe.id)
                findNavController().navigate(action)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@RecipeListFragment.adapter
        }

        // Observe recipes
        viewModel.allRecipes.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
            binding.emptyView.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}