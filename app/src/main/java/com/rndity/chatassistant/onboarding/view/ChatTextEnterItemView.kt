/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.rndity.chatassistant.R
import com.rndity.chatassistant.Item
import com.rndity.chatassistant.onboarding.TextEnterItem
import com.rndity.chatassistant.ItemMode
import kotlinx.android.synthetic.main.view_chat_text_enter_item.view.*

class ChatTextEnterItemView : ChatItemView {

    var textChangeListener: ((item: Item, text: String?) -> Unit)? = null

    private var item: TextEnterItem? = null

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
        LayoutInflater.from(context).inflate(R.layout.view_chat_text_enter_item, this, true)

        textEdit.setOnEditorActionListener({ textView, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    item?.let { textChangeListener?.invoke(it, textView.text.toString()) }
                    true
                }
                else -> false
            }
        })
    }

    fun showKeyboard() {
        textEdit.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun setItem(item: TextEnterItem, itemMode: ItemMode) {
        this.item = item
        textEdit.setText(item.text)
        textEdit.hint = item.hint
        showKeyboard()

        showOverlay = when (itemMode) {
            ItemMode.CURRENT_CONTEXT_ACTIVE -> false
            ItemMode.CURRENT_CONTEXT_RESTORE -> false
            ItemMode.PAST_CONTEXT_RESTORE -> true
        }
    }
}
