package io.esparta.library.extensions

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 *
 * Created by gmribas on 05/12/17.
 */

private class VarDelegate<T>(initializer: () -> T) : ReadWriteProperty<Any?, T> {

    private var initializer: (() -> T)? = initializer

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = initializer!!()
        }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

// http://stackoverflow.com/questions/34346966/kotlin-lazy-default-property/34347410#34347410
object DelegatesExt {
    fun <T> lazyVar(initializer: () -> T): ReadWriteProperty<Any?, T> = VarDelegate(initializer)
}

inline fun <T1: Any, T2: Any, R: Any> doubleLet(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? =
        if (p1 != null && p2 != null) block(p1, p2) else null

inline fun <T1: Any, T2: Any, T3: Any, R: Any> tripleLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3)->R?): R? =
        if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null