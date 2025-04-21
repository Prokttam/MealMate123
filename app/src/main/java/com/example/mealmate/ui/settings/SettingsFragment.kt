package com.example.mealmate.ui.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mealmate.R
import com.example.mealmate.databinding.FragmentSettingsBinding
import com.example.mealmate.ui.auth.LoginActivity
import com.example.mealmate.ui.auth.UserViewModel
import com.example.mealmate.util.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null

    // Image selection launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            loadProfileImage(selectedImageUri.toString())
        }
    }

    // Camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                loadProfileImage(path)
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
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user data
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                binding.tvUserEmail.text = it.email

                // Load profile image if available
                if (!it.profileImage.isNullOrEmpty()) {
                    loadProfileImage(it.profileImage)
                }
            }
        }

        // Setup profile image picker
        binding.ivProfileImage.setOnClickListener {
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

        // Setup buttons
        binding.btnLogout.setOnClickListener {
            viewModel.logoutUser()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Save notification preference
            // In a real app, you'd save this to preferences
            Toast.makeText(
                requireContext(),
                "Notifications ${if (isChecked) "enabled" else "disabled"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Setup theme switch
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            // Save theme preference
            // In a real app, you'd apply the theme change
            Toast.makeText(
                requireContext(),
                "Dark theme ${if (isChecked) "enabled" else "disabled"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openImageOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
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

    private fun loadProfileImage(imagePath: String?) {
        if (imagePath.isNullOrEmpty()) return

        if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {
            Glide.with(this)
                .load(Uri.parse(imagePath))
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(binding.ivProfileImage)
        } else {
            val file = File(imagePath)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.ivProfileImage)
            }
        }

        // Save profile image path
        viewModel.currentUser.value?.let { user ->
            val updatedUser = user.copy(profileImage = imagePath)
            lifecycleScope.launch {
                // Replace updateCurrentUser with repository update method
                viewModel.updateCurrentUser(updatedUser)
                Toast.makeText(
                    requireContext(),
                    "Profile picture updated",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}