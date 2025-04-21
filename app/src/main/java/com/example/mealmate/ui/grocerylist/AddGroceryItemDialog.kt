// AddGroceryItemDialog.kt
package com.example.mealmate.ui.grocerylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.mealmate.data.local.entity.GroceryItem
import com.example.mealmate.databinding.DialogAddGroceryItemBinding
import com.example.mealmate.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddGroceryItemDialog : DialogFragment() {
    private var _binding: DialogAddGroceryItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GroceryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddGroceryItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[GroceryViewModel::class.java]

        // Setup category spinner
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            Constants.GROCERY_CATEGORIES
        )
        binding.spinnerCategory.adapter = categoryAdapter

        // Setup unit spinner
        val unitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            Constants.MEASUREMENT_UNITS
        )
        binding.spinnerUnit.adapter = unitAdapter

        // Setup buttons
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnAdd.setOnClickListener {
            addGroceryItem()
        }
    }

    private fun addGroceryItem() {
        val name = binding.etItemName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etItemName.error = "Name is required"
            return
        }

        val category = binding.spinnerCategory.selectedItem.toString()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val quantity = if (quantityStr.isNotEmpty()) {
            quantityStr.toFloatOrNull() ?: 0f
        } else {
            0f
        }
        val unit = binding.spinnerUnit.selectedItem.toString()

        val item = GroceryItem(
            userId = 0, // Will be set by ViewModel
            name = name,
            category = category,
            quantity = quantity,
            unit = unit
        )

        viewModel.insert(item)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}