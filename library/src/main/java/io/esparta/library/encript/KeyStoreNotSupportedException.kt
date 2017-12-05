package io.esparta.library.encript

import android.os.Build

/**
 *
 * Created by gmribas on 15/11/17.
 * Indicates that the device version is lower than [Build.VERSION_CODES.JELLY_BEAN_MR2]
 */
class KeyStoreNotSupportedException : Throwable()