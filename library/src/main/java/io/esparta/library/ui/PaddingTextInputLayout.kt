package io.esparta.library.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.esparta.library.R


/**
 *
 * Created by gmribas on 19/09/17.
 */
class PaddingTextInputLayout: TextInputLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private val paddingView by lazy { View(context) }
    private var paddingHeight = 20f

    private fun init (attrs: AttributeSet?) {
        if (isInEditMode) {
            paddingHeight = 20f
            return
        }

        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.PaddingTextInputLayout, 0, 0)
            try {
                paddingHeight = ta.getDimension(R.styleable.PaddingTextInputLayout_hint_padding, 20.0f)
            } finally {
                ta.recycle()
            }
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        refreshPaddingView()
    }

    private fun addPaddingView() {
        val paddingParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, paddingHeight.toInt())
        super.addView(paddingView, 0, paddingParams)
    }

    private fun refreshPaddingView() {
        removeView(paddingView)
        addPaddingView()
    }
}