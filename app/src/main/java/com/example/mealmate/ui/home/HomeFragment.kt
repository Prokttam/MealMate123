package com.example.mealmate.ui.home

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.R
import com.example.mealmate.databinding.FragmentHomeBinding
import com.example.mealmate.ui.recipes.RecipeAdapter
import com.example.mealmate.util.DateTimeUtils
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var recentRecipeAdapter: RecipeAdapter
    private lateinit var weeklyPlanAdapter: WeeklyPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Setup recipe adapter
        recentRecipeAdapter = RecipeAdapter(
            onItemClick = { recipe ->
                val action = HomeFragmentDirections.actionHomeToRecipeDetail(recipe.id)
                findNavController().navigate(action)
            }
        )

        binding.recyclerRecentMeals.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentRecipeAdapter
        }

        // Setup weekly plan adapter
        weeklyPlanAdapter = WeeklyPlanAdapter()
        binding.recyclerWeeklyPlan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = weeklyPlanAdapter
        }

        // Observe recent recipes
        viewModel.recentRecipes.observe(viewLifecycleOwner) { recipes ->
            recentRecipeAdapter.submitList(recipes.take(5))
            binding.noRecipesText.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe week start date
        viewModel.weekStartDate.observe(viewLifecycleOwner) { startDate ->
            val weekDays = viewModel.getWeekDays()
            weeklyPlanAdapter.submitList(weekDays)

            // Update week text using DateFormat
            val endDate = DateTimeUtils.getEndOfWeek(startDate)

            val startDateObj = Date(startDate)
            val endDateObj = Date(endDate)

            val startString = DateFormat.format("MMM dd", startDateObj)
            val endString = DateFormat.format("MMM dd, yyyy", endDateObj)

            binding.tvWeekRange.text = "$startString - $endString"
        }

        // Setup navigation buttons
        binding.btnPreviousWeek.setOnClickListener {
            viewModel.moveWeekBackward()
        }

        binding.btnNextWeek.setOnClickListener {
            viewModel.moveWeekForward()
        }

        // Setup quick action buttons
        binding.cardCreateMeal.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_create_recipe)
        }

        binding.cardGroceryList.setOnClickListener {
            findNavController().navigate(R.id.navigation_grocery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}