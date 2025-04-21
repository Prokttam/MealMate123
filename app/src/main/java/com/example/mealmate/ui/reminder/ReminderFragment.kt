package com.example.mealmate.ui.reminder

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.R
import com.example.mealmate.databinding.FragmentReminderBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class ReminderFragment : Fragment() {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        // Setup ViewPager2 with tabs
        val pagerAdapter = ReminderPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Upcoming"
                1 -> "Past"
                else -> ""
            }
        }.attach()

        // Setup FAB
        binding.fab.setOnClickListener {
            val dialog = AddReminderDialog()
            dialog.show(parentFragmentManager, "AddReminderDialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_reminder, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_completed -> {
                showClearCompletedDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showClearCompletedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear Completed Reminders")
            .setMessage("Are you sure you want to delete all completed reminders?")
            .setPositiveButton("Clear") { _, _ ->
                viewModel.deleteCompletedReminders()
                Toast.makeText(
                    requireContext(),
                    "Completed reminders cleared",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}