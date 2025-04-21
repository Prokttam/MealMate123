// GroceryListFragment.kt
package com.example.mealmate.ui.grocerylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.data.local.entity.GroceryItem
import com.example.mealmate.databinding.FragmentGroceryListBinding
import com.example.mealmate.util.ShakeDetector
import com.example.mealmate.util.SwipeGestureHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class GroceryListFragment : Fragment() {
    private var _binding: FragmentGroceryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GroceryViewModel
    private lateinit var adapter: GroceryListAdapter
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroceryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[GroceryViewModel::class.java]

        // Initialize adapter
        adapter = GroceryListAdapter()

        // Setup RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@GroceryListFragment.adapter
        }

        // Setup swipe gestures
        val swipeGestureHelper = SwipeGestureHelper(
            requireContext(),
            onSwiped = { position, viewHolder, direction ->
                val item = adapter.currentList[position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Swipe left to delete
                        viewModel.delete(item)
                        Snackbar.make(
                            binding.root,
                            "Item deleted",
                            Snackbar.LENGTH_LONG
                        ).setAction("Undo") {
                            viewModel.insert(item)
                        }.show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        // Swipe right to mark as purchased
                        viewModel.togglePurchased(item)

                        // Update the view holder to reflect the new state
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        )

        ItemTouchHelper(swipeGestureHelper).attachToRecyclerView(binding.recyclerView)

        // Setup shake detector to clear purchased items
        shakeDetector = ShakeDetector(requireContext()) {
            val purchasedItems = adapter.currentList.filter { it.isPurchased }
            if (purchasedItems.isNotEmpty()) {
                showClearPurchasedDialog(purchasedItems.size)
            }
        }

        // Observe grocery items
        viewModel.allGroceryItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        // Setup delegate button
        binding.btnDelegate.setOnClickListener {
            val unpurchasedItems = adapter.currentList.filter { !it.isPurchased }
            if (unpurchasedItems.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "No items to delegate",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showDelegateDialog(unpurchasedItems)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }

    private fun showClearPurchasedDialog(count: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear Purchased Items")
            .setMessage("Remove $count purchased items from your grocery list?")
            .setPositiveButton("Clear") { _, _ ->
                viewModel.deletePurchasedItems()
                Toast.makeText(
                    requireContext(),
                    "Purchased items cleared",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDelegateDialog(items: List<GroceryItem>) {
        // Convert List<GroceryItem> to ArrayList<GroceryItem> explicitly
        val groceryArrayList = ArrayList<GroceryItem>()
        groceryArrayList.addAll(items)

        val dialog = DelegateShoppingDialog.newInstance(groceryArrayList)
        dialog.show(parentFragmentManager, "DelegateShoppingDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}