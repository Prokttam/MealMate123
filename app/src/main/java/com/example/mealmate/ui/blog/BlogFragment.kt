package com.example.mealmate.ui.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.databinding.FragmentBlogBinding

class BlogFragment : Fragment() {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BlogViewModel
    private lateinit var adapter: BlogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[BlogViewModel::class.java]

        // Setup adapter
        adapter = BlogAdapter(
            onItemClick = { recipe ->
                val action = BlogFragmentDirections.actionBlogToRecipeDetail(recipe.id)
                findNavController().navigate(action)
            },
            onSaveClick = { recipe ->
                viewModel.saveRecipeToMyCollection(recipe)
                Toast.makeText(
                    requireContext(),
                    "Recipe saved to your collection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BlogFragment.adapter
        }

        // Observe public recipes
        viewModel.publicRecipes.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
            binding.emptyView.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}