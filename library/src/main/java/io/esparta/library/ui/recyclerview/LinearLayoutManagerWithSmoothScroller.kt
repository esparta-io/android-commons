package io.esparta.library.ui.recyclerview

import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 *
 * Created by gmribas on 12/07/17.
 */
class LinearLayoutManagerWithSmoothScroller: LinearLayoutManager {

    private var smoothScroller by Delegates.notNull<TopSnappedSmoothScroller>()
    private var callback: ScrollCallback? = null
    private var tickerDisposable: Disposable? = null

    constructor(context: Context, orientation: Int, reverseLayout: Boolean): super(context, orientation, reverseLayout) {
        smoothScroller = TopSnappedSmoothScroller(context)
    }

    constructor(context: Context, orientation: Int, reverseLayout: Boolean, callback: ScrollCallback): super(context, orientation, reverseLayout) {
        smoothScroller = TopSnappedSmoothScroller(context)
        this.callback = callback
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        smoothScroller.targetPosition = position
        callback?.let { registerTimer(position) }
        startSmoothScroll(smoothScroller)
    }

    private inner class TopSnappedSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@LinearLayoutManagerWithSmoothScroller.computeScrollVectorForPosition(targetPosition)
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    private fun registerTimer(position: Int) {
        tickerDisposable = Observable
                .interval(500, TimeUnit.MILLISECONDS)
                .filter { ! smoothScroller.isRunning }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            callback?.onScrollFinished(position)
                            tickerDisposable?.dispose()
                        }
                )
    }

    fun setScrollCallback(callback: ScrollCallback?) {
        this.callback = callback
    }

    interface ScrollCallback {
        fun onScrollFinished(position: Int)
    }
}