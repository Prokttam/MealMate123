package com.example.mealmate.ui.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mealmate.data.local.entity.Reminder
import com.example.mealmate.databinding.DialogAddReminderBinding
import com.example.mealmate.ui.recipes.RecipeViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class AddReminderDialog(private val selectedRecipeId: Int? = null) : DialogFragment() {
    private var _binding: DialogAddReminderBinding? = null
    private val binding get() = _binding!!

    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var recipeViewModel: RecipeViewModel

    private var selectedDateTime: Calendar = Calendar.getInstance().apply {
        add(Calendar.HOUR, 1) // Default to 1 hour from now
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        reminderViewModel = ViewModelProvider(this)[ReminderViewModel::class.java]
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Load recipe name if ID provided
        selectedRecipeId?.let { recipeId ->
            binding.etTitle.isEnabled = false // Lock title field when recipe is provided
            binding.loadingRecipe.visibility = View.VISIBLE

            lifecycleScope.launch {
                val recipe = recipeViewModel.getRecipeByIdSuspend(recipeId)
                binding.loadingRecipe.visibility = View.GONE

                if (recipe != null) {
                    binding.etTitle.setText("Cook ${recipe.name}")
                    binding.etMessage.setText("Time to prepare ${recipe.name}")
                }
            }
        }

        // Setup date picker
        binding.btnPickDate.setOnClickListener {
            showDatePicker()
        }

        // Setup time picker
        binding.btnPickTime.setOnClickListener {
            showTimePicker()
        }

        // Update date/time display
        updateDateTimeDisplay()

        // Setup buttons
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveReminder()
        }
    }

    private fun showDatePicker() {
        val year = selectedDateTime.get(Calendar.YEAR)
        val month = selectedDateTime.get(Calendar.MONTH)
        val day = selectedDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedDateTime.set(Calendar.YEAR, selectedYear)
            selectedDateTime.set(Calendar.MONTH, selectedMonth)
            selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay)
            updateDateTimeDisplay()
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val hour = selectedDateTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDateTime.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedDateTime.set(Calendar.MINUTE, selectedMinute)
            updateDateTimeDisplay()
        }, hour, minute, false).show()
    }

    private fun updateDateTimeDisplay() {
        binding.tvSelectedDate.text = android.text.format.DateFormat.getDateFormat(requireContext())
            .format(selectedDateTime.time)
        binding.tvSelectedTime.text = android.text.format.DateFormat.getTimeFormat(requireContext())
            .format(selectedDateTime.time)
    }

    private fun saveReminder() {
        val title = binding.etTitle.text.toString().trim()
        val message = binding.etMessage.text.toString().trim()

        // Validate input
        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }

        // Validate date (must be in the future)
        val now = Calendar.getInstance()
        if (selectedDateTime.before(now)) {
            Toast.makeText(
                requireContext(),
                "Please select a future date/time",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Create reminder
        val reminder = Reminder(
            userId = 0, // Will be set by ViewModel
            title = title,
            message = message,
            reminderTime = selectedDateTime.timeInMillis,
            recipeId = selectedRecipeId
        )

        // Save reminder
        reminderViewModel.scheduleReminder(reminder)

        Toast.makeText(
            requireContext(),
            "Reminder set for ${binding.tvSelectedDate.text} at ${binding.tvSelectedTime.text}",
            Toast.LENGTH_SHORT
        ).show()

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Factory method to create the dialog with or without a recipe ID
        fun newInstance(recipeId: Int? = null) = AddReminderDialog(recipeId)
    }
}