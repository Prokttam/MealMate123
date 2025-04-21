package com.example.mealmate.util

object Constants {
    const val DATABASE_NAME = "mealmate_database"

    // Intent & Bundle keys
    const val EXTRA_RECIPE_ID = "recipe_id"
    const val EXTRA_GROCERY_ITEM_ID = "grocery_item_id"
    const val EXTRA_REMINDER_ID = "reminder_id"

    // Permission request codes
    const val PERMISSION_REQUEST_CODE_CAMERA = 100
    const val PERMISSION_REQUEST_CODE_STORAGE = 101
    const val PERMISSION_REQUEST_CODE_CONTACTS = 102
    const val PERMISSION_REQUEST_CODE_SMS = 103
    const val PERMISSION_REQUEST_CODE_NOTIFICATION = 104

    // Activity request codes
    const val REQUEST_CODE_IMAGE_CAPTURE = 200
    const val REQUEST_CODE_PICK_IMAGE = 201
    const val REQUEST_CODE_PICK_CONTACT = 202

    // Channel IDs
    const val NOTIFICATION_CHANNEL_ID = "meal_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Meal Reminders"

    // Meal types
    val MEAL_TYPES = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // Grocery categories
    val GROCERY_CATEGORIES = listOf(
        "Fruits & Vegetables",
        "Meat & Seafood",
        "Dairy & Eggs",
        "Bakery",
        "Pantry",
        "Frozen",
        "Beverages",
        "Other"
    )

    // Common units of measurement
    val MEASUREMENT_UNITS = listOf(
        "pcs",
        "g",
        "kg",
        "ml",
        "l",
        "tbsp",
        "tsp",
        "cup",
        "oz",
        "lb"
    )
}