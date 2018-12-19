/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.utils

fun <E> List<E>.split(index: Int): Pair<List<E>, List<E>> {
    val first = mutableListOf<E>()
    val second = mutableListOf<E>()
    for (idx in 0 until size) {
        if (idx < index) {
            first.add(get(idx))
        } else {
            second.add(get(idx))
        }
    }
    return Pair(first, second)
}
