// UpcomingRemindersFragment.kt
package com.example.mealmate.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.databinding.FragmentReminderListBinding

class UpcomingRemindersFragment : Fragment() {
    private var _binding: FragmentReminderListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReminderViewModel
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireParentFragment())[ReminderViewModel::class.java]

        // Initialize adapter
        adapter = ReminderAdapter(
            onItemClick = { reminder ->
                // View reminder details
            },
            onCompleteClick = { reminder ->
                viewModel.markReminderAsCompleted(reminder)
            },
            onDeleteClick = { reminder ->
                viewModel.deleteReminder(reminder)
            }
        )

        // Setup RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@UpcomingRemindersFragment.adapter
        }

        // Observe upcoming reminders
        viewModel.upcomingReminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
            binding.emptyView.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}