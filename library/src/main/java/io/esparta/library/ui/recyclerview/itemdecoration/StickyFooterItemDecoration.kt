package io.esparta.library.ui.recyclerview.itemdecoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


/**
 *
 * Created by gmribas on 25/09/17.
 */
class StickyFooterItemDecoration : RecyclerView.ItemDecoration() {

    /**
     * Top offset to completely hide footer from the screen and therefore avoid noticeable blink during changing position of the footer.
     */
    @Suppress("PrivatePropertyName")
    private val OFF_SCREEN_OFFSET = 5000

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val adapterItemCount = parent.adapter.itemCount
        if (isFooter(parent, view, adapterItemCount)) {
            //For the first time, each view doesn't contain any parameters related to its size,
            //hence we can't calculate the appropriate offset.
            //In this case, set a big top offset and notify adapter to update footer one more time.
            //Also, we shouldn't do it if footer became visible after scrolling.
            if (view.height == 0 && state.didStructureChange()) {
                hideFooterAndUpdate(outRect, view, parent)
            } else {
                outRect.set(0, calculateTopOffset(parent, view, adapterItemCount), 0, 0)
            }
        }
    }

    private fun hideFooterAndUpdate(outRect: Rect, footerView: View, parent: RecyclerView) {
        outRect.set(0, OFF_SCREEN_OFFSET, 0, 0)
        footerView.post { parent.adapter.notifyDataSetChanged() }
    }

    private fun calculateTopOffset(parent: RecyclerView, footerView: View, itemCount: Int): Int {
        val topOffset = parent.height - visibleChildsHeightWithFooter(parent, footerView, itemCount)
        return if (topOffset < 0) 0 else topOffset
    }

    private fun visibleChildsHeightWithFooter(parent: RecyclerView, footerView: View, itemCount: Int): Int {
        //In the case of dynamic content when adding or removing are possible itemCount from the adapter is reliable,
        //but when the screen can fit fewer items than in adapter, getChildCount() from RecyclerView should be used.
        val onScreenItemCount = Math.min(parent.childCount, itemCount)
        val totalHeight = (0 until onScreenItemCount - 1).sumBy { parent.getChildAt(it).height }
        return totalHeight + footerView.height
    }

    private fun isFooter(parent: RecyclerView, view: View, itemCount: Int): Boolean = parent.getChildAdapterPosition(view) == itemCount - 1
}