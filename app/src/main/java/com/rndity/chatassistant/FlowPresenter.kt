/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant

import com.rndity.chatassistant.animator.*
import com.rndity.chatassistant.animator.add.AlphaAddChatItemAnimator
import com.rndity.chatassistant.animator.clear.SlideUpClearChatItemAnimator
import com.rndity.chatassistant.animator.contextChange.*
import com.rndity.chatassistant.animator.keep.SlideUpKeepChatItemAnimator
import com.rndity.chatassistant.animator.replace.AlphaReplaceChatItemAnimator

interface FlowPresenter {

    var flowListener: FlowListener?

    fun getState(): FlowPresenterState
    fun restoreState(state: FlowPresenterState)

    fun setItems(vararg items: Item)
    fun addItems(vararg items: Item, delay: Long = 0, animator: AddChatItemAnimator? = AlphaAddChatItemAnimator(), action: PendingAction? = null)
    fun replaceItem(oldItem: Item, newItem: Item, delay: Long = 0, animator: ReplaceChatItemAnimator? = AlphaReplaceChatItemAnimator(), action: PendingAction? = null)
    fun replaceLastItem(newItem: Item, delay: Long = 0, animator: ReplaceChatItemAnimator? = AlphaReplaceChatItemAnimator(), action: PendingAction? = null)
    fun clearItems(moveToHistory: Boolean = true, delay: Long = 0, animator: ClearChatItemAnimator? = SlideUpClearChatItemAnimator(), action: PendingAction? = null)
    fun clearToItem(item: Item, moveToHistory: Boolean = true, includeItemInHistory: Boolean = true, delay: Long = 0, animator: KeepChatItemAnimator? = SlideUpKeepChatItemAnimator(), action: PendingAction? = null)
    fun keepsLastItems(count: Int, moveToHistory: Boolean = true, includeItemsInHistory: Boolean = true, delay: Long = 0, animator: KeepChatItemAnimator? = SlideUpKeepChatItemAnimator(), action: PendingAction? = null)

    fun historySize(): Int
    fun currentHistoryPage(): Int?
    fun showHistoryPage(pageIndex: Int, delay: Long = 0, animator: ContextChangeItemAnimator? = SlideTopContextChangeItemAnimator(), action: PendingAction? = null)
    fun showActivePage(delay: Long = 0, animator: ContextChangeItemAnimator? = SlideTopContextChangeItemAnimator(), action: PendingAction? = null)

}
