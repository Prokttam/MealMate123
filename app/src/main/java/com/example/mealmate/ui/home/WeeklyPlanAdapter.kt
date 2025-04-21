package com.example.mealmate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.databinding.ItemWeekDayBinding
import com.example.mealmate.util.DateTimeUtils
import java.util.Calendar

class WeeklyPlanAdapter : ListAdapter<Long, WeeklyPlanAdapter.WeekDayViewHolder>(WeekDayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayViewHolder {
        val binding = ItemWeekDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeekDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WeekDayViewHolder(private val binding: ItemWeekDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dayTimestamp: Long) {
            val today = Calendar.getInstance().timeInMillis
            val isToday = DateTimeUtils.getStartOfDay(today) == DateTimeUtils.getStartOfDay(dayTimestamp)

            binding.tvWeekDay.text = DateTimeUtils.formatFriendlyDate(dayTimestamp)
            binding.todayIndicator.visibility = if (isToday) View.VISIBLE else View.GONE

            // In a real app, you would load meal plans for this day
            // For now, we'll just show empty slots
            binding.breakfastSlot.tvMealSlot.text = "Breakfast"
            binding.lunchSlot.tvMealSlot.text = "Lunch"
            binding.dinnerSlot.tvMealSlot.text = "Dinner"

            // Set up click listeners for adding meals
            binding.breakfastSlot.btnAddMeal.setOnClickListener {
                // Navigate to recipe selection for breakfast
            }

            binding.lunchSlot.btnAddMeal.setOnClickListener {
                // Navigate to recipe selection for lunch
            }

            binding.dinnerSlot.btnAddMeal.setOnClickListener {
                // Navigate to recipe selection for dinner
            }
        }
    }

    class WeekDayDiffCallback : DiffUtil.ItemCallback<Long>() {
        override fun areItemsTheSame(oldItem: Long, newItem: Long): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Long, newItem: Long): Boolean {
            return oldItem == newItem
        }
    }
}