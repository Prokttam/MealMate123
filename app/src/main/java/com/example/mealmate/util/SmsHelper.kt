package com.example.mealmate.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.example.mealmate.data.local.entity.GroceryItem

object SmsHelper {
    fun sendShoppingList(
        context: Context,
        phoneNumber: String,
        items: List<GroceryItem>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (phoneNumber.isEmpty()) {
            onError(IllegalArgumentException("Phone number is required"))
            return
        }

        if (items.isEmpty()) {
            onError(IllegalArgumentException("Shopping list is empty"))
            return
        }

        // Check for permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            onError(SecurityException("SMS permission not granted"))
            return
        }

        try {
            val message = buildString {
                append("Shopping List from MealMate:\n\n")

                val groupedItems = items.groupBy { it.category }
                groupedItems.forEach { (category, categoryItems) ->
                    append("${category.uppercase()}:\n")
                    categoryItems.forEach { item ->
                        append("- ${item.name}")
                        if (item.quantity > 0) {
                            append(" (${item.quantity} ${item.unit})")
                        }
                        append("\n")
                    }
                    append("\n")
                }
            }

            val smsManager = SmsManager.getDefault()
            val messageParts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null)

            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }
}