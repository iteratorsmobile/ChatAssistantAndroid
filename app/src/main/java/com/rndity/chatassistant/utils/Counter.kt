/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.utils

import kotlin.reflect.KProperty

class Counter(private var value: Int = Int.MIN_VALUE) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        ++value
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, i: Int) {
        value = i
    }
}
