package io.esparta.library.ui.extensions

import android.content.Context
import android.net.ConnectivityManager

/**
 *
 * Created by gmribas on 05/12/17.
 */
fun Context.isConnected(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return manager.activeNetworkInfo?.isConnectedOrConnecting ?: true
}