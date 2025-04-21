// CreateRecipeFragment.kt
package com.example.mealmate.ui.recipes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mealmate.R
import com.example.mealmate.data.local.entity.Recipe
import com.example.mealmate.databinding.FragmentCreateRecipeBinding
import com.example.mealmate.util.Constants
import com.example.mealmate.util.FileUtils
import java.io.File
import androidx.appcompat.app.AlertDialog


class CreateRecipeFragment : Fragment() {
    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeViewModel
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null

    // Image selection launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.ivRecipeImage.setImageURI(selectedImageUri)
            binding.ivRecipeImage.visibility = View.VISIBLE
            binding.btnAddImage.text = getString(R.string.change_image)
        }
    }

    // Camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                binding.ivRecipeImage.setImageURI(Uri.fromFile(File(path)))
                binding.ivRecipeImage.visibility = View.VISIBLE
                binding.btnAddImage.text = getString(R.string.change_image)
            }
        }
    }

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImageOptions()
        } else {
            Toast.makeText(
                requireContext(),
                "Storage permission is required to select images",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Setup image picker
        binding.btnAddImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openImageOptions()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Setup save button
        binding.btnSaveRecipe.setOnClickListener {
            saveRecipe()
        }

        // Setup ingredient button
        binding.btnAddIngredient.setOnClickListener {
            showAddIngredientDialog()
        }
    }

    private fun openImageOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
        builder.show()
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    FileUtils.createImageFile(requireContext()).also {
                        currentPhotoPath = it.absolutePath
                    }
                } catch (ex: Exception) {
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.mealmate.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun showAddIngredientDialog() {
        val dialog = AddIngredientDialog()
        dialog.setOnIngredientAddedListener { ingredient ->val currentText = binding.etIngredients.text.toString()
            val newText = if (currentText.isEmpty()) {
                ingredient
            } else {
                "$currentText\n$ingredient"
            }
            binding.etIngredients.setText(newText)
        }
        dialog.show(parentFragmentManager, "AddIngredientDialog")
    }

    private fun saveRecipe() {
        val name = binding.etRecipeName.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()
        val instructions = binding.etInstructions.text.toString().trim()

        // Validate input
        if (name.isEmpty()) {
            binding.etRecipeName.error = "Recipe name is required"
            return
        }

        if (ingredients.isEmpty()) {
            binding.etIngredients.error = "At least one ingredient is required"
            return
        }

        if (instructions.isEmpty()) {
            binding.etInstructions.error = "Instructions are required"
            return
        }

        // Process image
        val imagePath = when {
            currentPhotoPath != null -> currentPhotoPath!!
            selectedImageUri != null -> FileUtils.saveImageToInternalStorage(
                requireContext(),
                selectedImageUri!!
            ) ?: ""
            else -> ""
        }

        // Create and save recipe
        val recipe = Recipe(
            userId = 0, // Will be set by ViewModel
            name = name,
            ingredients = ingredients,
            instructions = instructions,
            imageUri = imagePath,
            isPublic = binding.cbShareRecipe.isChecked
        )

        viewModel.insert(recipe)

        // Show success message
        Toast.makeText(
            requireContext(),
            "Recipe saved successfully",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate back
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
            