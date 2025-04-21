// MainActivity.kt
package com.example.mealmate.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mealmate.MealMateApplication
import com.example.mealmate.R
import com.example.mealmate.databinding.ActivityMainBinding
import com.example.mealmate.ui.auth.LoginActivity
import com.example.mealmate.ui.auth.UserViewModel
import com.example.mealmate.ui.grocerylist.AddGroceryItemDialog
import com.example.mealmate.util.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Check if user is logged in
        val sessionManager = (application as MealMateApplication).sessionManager
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Setup toolbar
        setSupportActionBar(binding.toolbar)

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation with nav controller
        binding.bottomNav.setupWithNavController(navController)

        // Define top-level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_recipes,
                R.id.navigation_grocery,
                R.id.navigation_blog,
                R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Observe destination changes to show/hide FAB
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_recipes, R.id.navigation_grocery -> {
                    binding.fab.show()

                    // Set FAB icon based on destination
                    if (destination.id == R.id.navigation_recipes) {
                        binding.fab.setImageResource(R.drawable.ic_add_recipe)
                    } else {
                        binding.fab.setImageResource(R.drawable.ic_add_grocery)
                    }
                }
                else -> binding.fab.hide()
            }
        }

        // Set FAB click listener
        binding.fab.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_recipes -> {
                    navController.navigate(R.id.action_navigation_recipes_to_create_recipe)
                }
                R.id.navigation_grocery -> {
                    showAddGroceryItemDialog()
                }
            }
        }

        // Request permissions
        requestRequiredPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                userViewModel.logoutUser()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showAddGroceryItemDialog() {
        val dialog = AddGroceryItemDialog()
        dialog.show(supportFragmentManager, "AddGroceryItemDialog")
    }

    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // SMS permission for delegation feature
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS)
        }

        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                Constants.PERMISSION_REQUEST_CODE_SMS
            )
        }
    }
}