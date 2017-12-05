package io.esparta.library.ui.extensions

import android.graphics.PorterDuff
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 *
 * Created by gmribas on 05/12/17.
 */

fun View.enableView() {
    enableDisableView(true)
}

fun View.disableView() {
    enableDisableView(false)
}

fun View.enableDisableView(enabled: Boolean) {
    this.isEnabled = enabled
    this.isClickable = enabled
    this.alpha = if (enabled) 1f else .5f
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun View.fadeIn() {
    this.animate().alpha(1f).start()
}

fun View.fadeOut() {
    this.animate().alpha(0f).start()
}

/**
 * Define color filter no background
 */
fun EditText.setUnderlineColor(@ColorRes color: Int) {
    background.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
}

fun ProgressBar.setProgressColor(@ColorRes color: Int) {
    indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.MULTIPLY)
}