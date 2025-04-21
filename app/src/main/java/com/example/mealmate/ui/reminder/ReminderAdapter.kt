// ReminderAdapter.kt
package com.example.mealmate.ui.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.data.local.entity.Reminder
import com.example.mealmate.databinding.ItemReminderBinding
import com.example.mealmate.util.DateTimeUtils

class ReminderAdapter(
    private val onItemClick: (Reminder) -> Unit,
    private val onCompleteClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnComplete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCompleteClick(getItem(position))
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(reminder: Reminder) {
            binding.tvTitle.text = reminder.title
            binding.tvMessage.text = reminder.message
            binding.tvDateTime.text = DateTimeUtils.formatDateTime(reminder.reminderTime)

            // Show/hide completed status
            if (reminder.isCompleted) {
                binding.tvCompletedStatus.visibility = View.VISIBLE
                binding.btnComplete.visibility = View.GONE
            } else {
                binding.tvCompletedStatus.visibility = View.GONE
                binding.btnComplete.visibility = View.VISIBLE
            }

            // Format date with friendly string
            val friendlyDate = DateTimeUtils.formatFriendlyDate(reminder.reminderTime)
            val friendlyTime = DateTimeUtils.formatFriendlyTime(reminder.reminderTime)
            binding.tvDateTime.text = "$friendlyDate at $friendlyTime"
        }
    }

    class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }
}