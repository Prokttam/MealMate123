package com.example.mealmate.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R

class SwipeGestureHelper(
    private val context: Context,
    private val onSwiped: (Int, RecyclerView.ViewHolder, Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteBackground = ColorDrawable(ContextCompat.getColor(context, R.color.error))
    private val purchaseBackground = ColorDrawable(ContextCompat.getColor(context, R.color.primary))
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val checkIcon = ContextCompat.getDrawable(context, R.drawable.ic_check)
    private val paint = Paint()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false // We don't support moving items
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        onSwiped(position, viewHolder, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (dX > 0) { // Swiping right (mark as purchased)
            // Draw background
            purchaseBackground.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            purchaseBackground.draw(c)

            // Draw check icon
            val iconMargin = (itemHeight - checkIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + checkIcon.intrinsicHeight
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + checkIcon.intrinsicWidth

            checkIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            checkIcon.setTint(Color.WHITE)
            checkIcon.draw(c)

            // Draw text
            paint.color = Color.WHITE
            paint.textSize = 42f
            paint.textAlign = Paint.Align.LEFT
            c.drawText(
                "Complete",
                (iconRight + 16).toFloat(),
                (itemView.top + itemHeight / 2 + 12).toFloat(),
                paint
            )

        } else if (dX < 0) { // Swiping left (delete)
            // Draw background
            deleteBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            deleteBackground.draw(c)

            // Draw delete icon
            val iconMargin = (itemHeight - deleteIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + deleteIcon.intrinsicHeight
            val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
            val iconRight = itemView.right - iconMargin

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            deleteIcon.setTint(Color.WHITE)
            deleteIcon.draw(c)

            // Draw text
            paint.color = Color.WHITE
            paint.textSize = 42f
            paint.textAlign = Paint.Align.RIGHT
            c.drawText(
                "Delete",
                (iconLeft - 16).toFloat(),
                (itemView.top + itemHeight / 2 + 12).toFloat(),
                paint
            )
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}