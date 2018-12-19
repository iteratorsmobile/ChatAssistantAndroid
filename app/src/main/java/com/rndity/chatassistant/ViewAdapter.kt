/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant

import android.view.View

interface ViewAdapter {
    fun viewForItem(item: Item, itemMode: ItemMode, parent: View): View
    fun releaseView(item: Item, view: View, parent: View) = Unit
}
