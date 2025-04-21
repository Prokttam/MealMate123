package com.example.mealmate.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.databinding.FragmentReminderListBinding

class PastRemindersFragment : Fragment() {
    private var _binding: FragmentReminderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReminderViewModel by viewModels()
    private lateinit var reminderAdapter: ReminderAdapter

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

        // Initialize adapter
        reminderAdapter = ReminderAdapter(
            onItemClick = { reminder ->
                // Handle item click - view reminder details
                // Implement detailed view or action as needed
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
            adapter = reminderAdapter
        }

        // Observe past reminders
        viewModel.pastReminders.observe(viewLifecycleOwner) { reminders ->
            reminderAdapter.submitList(reminders)
            binding.emptyView.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PastRemindersFragment {
            return PastRemindersFragment()
        }
    }
}