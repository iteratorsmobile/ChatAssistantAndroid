/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator

import android.view.View
import com.rndity.chatassistant.Size

sealed class ChatItemAnimator {
    fun defaultDuration() = 500L
}

abstract class AddChatItemAnimator : ChatItemAnimator() {
    abstract fun animate(views: List<View>, parentSize: Size): AnimatorHandler
    open fun onDone(views: List<View>) = Unit
}

abstract class ReplaceChatItemAnimator : ChatItemAnimator() {
    abstract fun animate(newView: View, oldView: View, parentSize: Size): AnimatorHandler
    open fun onDone(newView: View, oldView: View) = Unit
}

abstract class ClearChatItemAnimator : ChatItemAnimator() {
    abstract fun animate(viewsToClear: List<View>, parentSize: Size): AnimatorHandler
    open fun onDone(viewsToClear: List<View>) = Unit
}

abstract class KeepChatItemAnimator : ChatItemAnimator() {
    abstract fun animate(viewsToClear: List<View>, viewsToKeep: List<View>, parentSize: Size): AnimatorHandler
    open fun onDone(viewsToClear: List<View>, viewsToKeep: List<View>) = Unit
}

abstract class ContextChangeItemAnimator : ChatItemAnimator() {
    abstract fun animate(viewsToClear: List<View>, viewsToShow: List<View>, parentSize: Size): AnimatorHandler
    open fun onDone(viewsToClear: List<View>, viewsToKeep: List<View>) = Unit
}
