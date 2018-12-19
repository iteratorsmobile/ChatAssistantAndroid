/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.rndity.chatassistant.R
import com.rndity.chatassistant.onboarding.QuestionTextItem
import com.rndity.chatassistant.ItemMode
import kotlinx.android.synthetic.main.view_chat_text_item.view.*

class ChatTextItemView : ChatItemView {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.view_chat_text_item, this, true)
    }

    fun setItem(item: QuestionTextItem, itemMode: ItemMode) {
        textView.text = item.text
        showOverlay = when (itemMode) {
            ItemMode.CURRENT_CONTEXT_ACTIVE -> false
            ItemMode.CURRENT_CONTEXT_RESTORE -> false
            ItemMode.PAST_CONTEXT_RESTORE -> true
        }
    }

}
