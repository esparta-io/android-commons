package io.esparta.library.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

/**
 *
 * Created by gmribas on 01/11/17.
 */
fun getBitmapFromDrawable(context: Context, @DrawableRes id: Int): Bitmap {
    val vectorDrawable = ContextCompat.getDrawable(context, id)
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)
    return bm
}