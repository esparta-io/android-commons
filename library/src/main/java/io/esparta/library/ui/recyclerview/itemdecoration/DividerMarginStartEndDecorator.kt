package io.esparta.library.ui.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.support.v7.widget.LinearLayoutManager

/**
 * Created by gmribas on 30/09/17.
 * It is a copy of DividerItemDecoration, using margins
 */
class DividerMarginStartEndDecorator(val context: Context, private val mOrientation: Int = LinearLayoutManager.VERTICAL, private val leftInset: Int, private val rightInset: Int) : RecyclerView.ItemDecoration() {

    constructor(context: Context, orientation: Int, leftInset: Int = 0, rightInset: Int = 0, drawLastDivider: Boolean): this(context, orientation, leftInset, rightInset) {
        this.drawLastDivider = drawLastDivider
    }

    private var drawLastDivider = true

    var divider: Drawable? = null

    /**
     * Draws horizontal or vertical dividers onto the parent RecyclerView.
     *
     * @param canvas The [Canvas] onto which dividers will be drawn
     * @param parent The RecyclerView onto which dividers are being added
     * @param state The current RecyclerView.State of the RecyclerView
     */
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            drawHorizontalDividers(canvas, parent)
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVerticalDividers(canvas, parent)
        }
    }

    /**
     * Determines the size and location of offsets between items in the parent
     * RecyclerView.
     *
     * @param outRect The [Rect] of offsets to be added around the child
     * view
     * @param view The child view to be decorated with an offset
     * @param parent The RecyclerView onto which dividers are being added
     * @param state The current RecyclerView.State of the RecyclerView
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            return
        }

        divider?.let {
            val mOrientation = (parent.layoutManager as LinearLayoutManager).orientation
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.left = it.intrinsicWidth
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.top = it.intrinsicHeight
            }
        }
    }

    /**
     * Adds dividers to a RecyclerView with a LinearLayoutManager or its
     * subclass oriented horizontally.
     *
     * @param canvas The [Canvas] onto which horizontal dividers will be
     * drawn
     * @param parent The RecyclerView onto which horizontal dividers are being
     * added
     */
    private fun drawHorizontalDividers(canvas: Canvas, parent: RecyclerView) {
        divider?.let {
            val parentTop = parent.paddingTop
            val parentBottom = parent.height - parent.paddingBottom

            val limit = if (drawLastDivider) parent.childCount - 1 else parent.childCount - 2
            for (i in 0 until limit) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val parentLeft = leftInset + child.right + params.rightMargin
                val parentRight = rightInset + parentLeft + it.intrinsicWidth

                it.setBounds(parentLeft, parentTop, parentRight, parentBottom)
                it.draw(canvas)
            }
        }
    }

    /**
     * Adds dividers to a RecyclerView with a LinearLayoutManager or its
     * subclass oriented vertically.
     *
     * @param canvas The [Canvas] onto which vertical dividers will be
     * drawn
     * @param parent The RecyclerView onto which vertical dividers are being
     * added
     */
    private fun drawVerticalDividers(canvas: Canvas, parent: RecyclerView) {
        divider?.let {
            val parentLeft = leftInset + parent.paddingLeft
            val parentRight = parent.width - parent.paddingRight - rightInset

            val limit = if (drawLastDivider) parent.childCount - 1 else parent.childCount - 2
            for (i in 0 until limit) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val parentTop = child.bottom + params.bottomMargin
                val parentBottom = parentTop + it.intrinsicHeight

                it.setBounds(parentLeft, parentTop, parentRight, parentBottom)
                it.draw(canvas)
            }
        }
    }
}