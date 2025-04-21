package com.example.mealmate.ui.grocerylist

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.mealmate.data.local.entity.GroceryItem
import com.example.mealmate.databinding.DialogDelegateShoppingBinding
import com.example.mealmate.util.SmsHelper

class DelegateShoppingDialog : DialogFragment() {
    private var _binding: DialogDelegateShoppingBinding? = null
    private val binding get() = _binding!!

    private var groceryItems: List<GroceryItem> = emptyList()

    // Contact picker launcher
    private val pickContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val contactUri = result.data?.data ?: return@registerForActivityResult

            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            requireContext().contentResolver.query(
                contactUri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

                    val number = cursor.getString(numberIndex)
                    val name = cursor.getString(nameIndex)

                    binding.etContactName.setText(name)
                    binding.etPhoneNumber.setText(number)
                }
            }
        }
    }

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openContactPicker()
        } else {
            Toast.makeText(
                requireContext(),
                "Contact permission is required to select contacts",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val ARG_GROCERY_ITEMS = "grocery_items"

        fun newInstance(groceryItems: ArrayList<GroceryItem>): DelegateShoppingDialog {
            val args = Bundle().apply {
                putParcelableArrayList(ARG_GROCERY_ITEMS, groceryItems)
            }
            return DelegateShoppingDialog().apply {
                arguments = args
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelableArrayList<GroceryItem>(ARG_GROCERY_ITEMS)?.let {
            groceryItems = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDelegateShoppingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup contact picker
        binding.btnSelectContact.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openContactPicker()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }

        // Setup buttons
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSend.setOnClickListener {
            sendShoppingList()
        }

        // Update item count
        binding.tvItemCount.text = "Items to share: ${groceryItems.size}"
    }

    private fun openContactPicker() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        pickContactLauncher.launch(intent)
    }

    private fun sendShoppingList() {
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.error = "Phone number is required"
            return
        }

        // Check SMS permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(),
                "SMS permission is required to send shopping list",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Show progress
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSend.isEnabled = false

        // Send SMS
        SmsHelper.sendShoppingList(
            requireContext(),
            phoneNumber,
            groceryItems,
            onSuccess = {
                requireActivity().runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Shopping list sent successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
            },
            onError = { error ->
                requireActivity().runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSend.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Failed to send: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}