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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mealmate.R
import com.example.mealmate.data.local.entity.Recipe
import com.example.mealmate.databinding.FragmentCreateRecipeBinding
import com.example.mealmate.util.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class EditRecipeFragment : Fragment() {
    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeViewModel
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private var originalRecipe: Recipe? = null
    private val args: EditRecipeFragmentArgs by navArgs()

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

        // Change title and button text
        binding.tvTitle.text = "Edit Recipe"
        binding.btnSaveRecipe.text = "Update Recipe"

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
            updateRecipe()
        }

        // Setup ingredient button
        binding.btnAddIngredient.setOnClickListener {
            showAddIngredientDialog()
        }

        // Load existing recipe data
        lifecycleScope.launch {
            val recipe = viewModel.getRecipeByIdSuspend(args.recipeId)
            recipe?.let {
                originalRecipe = it
                populateRecipeData(it)
            }
        }
    }

    private fun populateRecipeData(recipe: Recipe) {
        binding.etRecipeName.setText(recipe.name)
        binding.etIngredients.setText(recipe.ingredients)
        binding.etInstructions.setText(recipe.instructions)
        binding.cbShareRecipe.isChecked = recipe.isPublic

        // Load image if available
        if (recipe.imageUri.isNotEmpty()) {
            val imageFile = File(recipe.imageUri)
            if (imageFile.exists()) {
                currentPhotoPath = recipe.imageUri
                binding.ivRecipeImage.setImageURI(Uri.fromFile(imageFile))
                binding.ivRecipeImage.visibility = View.VISIBLE
                binding.btnAddImage.text = getString(R.string.change_image)
            }
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
        dialog.setOnIngredientAddedListener { ingredient ->
            val currentText = binding.etIngredients.text.toString()
            val newText = if (currentText.isEmpty()) {
                ingredient
            } else {
                "$currentText\n$ingredient"
            }
            binding.etIngredients.setText(newText)
        }
        dialog.show(parentFragmentManager, "AddIngredientDialog")
    }

    private fun updateRecipe() {
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

        // Get original recipe
        originalRecipe?.let { recipe ->
            // Process image
            val imagePath = when {
                currentPhotoPath != null -> currentPhotoPath!!
                selectedImageUri != null -> FileUtils.saveImageToInternalStorage(
                    requireContext(),
                    selectedImageUri!!
                ) ?: recipe.imageUri // Keep original if new one fails
                else -> recipe.imageUri // Keep original
            }

            // Update recipe
            val updatedRecipe = recipe.copy(
                name = name,
                ingredients = ingredients,
                instructions = instructions,
                imageUri = imagePath,
                isPublic = binding.cbShareRecipe.isChecked
            )

            viewModel.update(updatedRecipe)

            // Show success message
            Toast.makeText(
                requireContext(),
                "Recipe updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            // Navigate back
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}