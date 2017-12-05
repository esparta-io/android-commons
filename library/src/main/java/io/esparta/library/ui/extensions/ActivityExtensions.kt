package io.esparta.library.ui.extensions

import android.os.Build
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager

/**
 *
 * Created by gmribas on 05/12/17.
 */
fun AppCompatActivity.enableDisableViews(enabled: Boolean, vararg views: View) {
    views.forEach {
        it.enableDisableView(enabled)
    }
}

fun AppCompatActivity.setViewsInvisible(vararg views: View) {
    views.forEach(View::setInvisible)
}

fun AppCompatActivity.setViewsVisible(vararg views: View) {
    views.forEach(View::setVisible)
}

fun AppCompatActivity.setViewsGone(vararg views: View) {
    views.forEach(View::setGone)
}

fun AppCompatActivity.snackbar(view: View, @StringRes text: Int, length: Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(view, text, length).show()
}

fun AppCompatActivity.snackbar(view: View, text: String, length: Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(view, text, length).show()
}

fun AppCompatActivity.snackbarAndAction(view: View, @StringRes text: Int, @StringRes actionText: Int, actionListener: View.OnClickListener, length: Int = Snackbar.LENGTH_INDEFINITE){
    Snackbar.make(view, text, length)
            .setAction(actionText, actionListener)
            .show()
}

fun AppCompatActivity.changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = color
    }
}