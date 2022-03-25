/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator.keep

import android.view.View
import android.view.ViewPropertyAnimator
import com.rndity.chatassistant.Size
import com.rndity.chatassistant.animator.KeepChatItemAnimator
import com.rndity.chatassistant.animator.AnimatorHandler
import com.rndity.chatassistant.animator.ViewPropertyAnimatorHandler

class SlideUpKeepChatItemAnimator : KeepChatItemAnimator() {

    override fun animate(viewsToClear: List<View>, viewsToKeep: List<View>, parentSize: Size): AnimatorHandler {
        val offset = viewsToClear.maxOf { view -> view.bottom }
        val result = mutableListOf<ViewPropertyAnimator>()
        for (view in viewsToClear) {
            result.add(view.animate().translationYBy(-offset.toFloat()).setDuration(defaultDuration()))
        }
        for (view in viewsToKeep) {
            result.add(view.animate().translationYBy(-offset.toFloat()).setDuration(defaultDuration()))
        }
        return ViewPropertyAnimatorHandler(result)
    }

    override fun onDone(viewsToClear: List<View>, viewsToKeep: List<View>) {
        for (view in viewsToKeep) {
            view.translationY = 0f
        }
    }
}