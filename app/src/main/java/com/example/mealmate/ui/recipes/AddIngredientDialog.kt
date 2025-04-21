// AddIngredientDialog.kt
package com.example.mealmate.ui.recipes

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.mealmate.R
import com.example.mealmate.databinding.DialogAddIngredientBinding
import com.example.mealmate.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddIngredientDialog : DialogFragment() {
    private var _binding: DialogAddIngredientBinding? = null
    private val binding get() = _binding!!

    private var onIngredientAddedListener: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddIngredientBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Ingredient")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                addIngredient()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    override fun onStart() {
        super.onStart()

        // Setup unit spinner
        val unitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            Constants.MEASUREMENT_UNITS
        )
        binding.spinnerUnit.adapter = unitAdapter
    }

    private fun addIngredient() {
        val name = binding.etIngredientName.text.toString().trim()
        if (name.isEmpty()) {
            return
        }

        val quantity = binding.etQuantity.text.toString().trim()
        val unit = binding.spinnerUnit.selectedItem.toString()

        // Format ingredient string
        val ingredient = if (quantity.isNotEmpty()) {
            "$quantity $unit $name"
        } else {
            name
        }

        onIngredientAddedListener?.invoke(ingredient)
    }

    fun setOnIngredientAddedListener(listener: (String) -> Unit) {
        onIngredientAddedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}