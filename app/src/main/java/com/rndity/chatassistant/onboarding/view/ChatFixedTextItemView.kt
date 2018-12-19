/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.rndity.chatassistant.R
import com.rndity.chatassistant.onboarding.TextFixedItem
import com.rndity.chatassistant.ItemMode
import kotlinx.android.synthetic.main.view_chat_fixed_text_item.view.*

class ChatFixedTextItemView : ChatItemView {

    constructor(context: Context) : super(context) {
        initialize(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.view_chat_fixed_text_item, this, true)
    }

    fun setItem(item: TextFixedItem, itemMode: ItemMode) {
        textEdit.setText(item.text)
        showOverlay = when (itemMode) {
            ItemMode.CURRENT_CONTEXT_ACTIVE -> false
            ItemMode.CURRENT_CONTEXT_RESTORE -> false
            ItemMode.PAST_CONTEXT_RESTORE -> true
        }
    }
}
