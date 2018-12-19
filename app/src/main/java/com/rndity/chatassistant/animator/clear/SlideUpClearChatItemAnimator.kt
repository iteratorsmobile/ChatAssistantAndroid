/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator.clear

import android.view.View
import android.view.ViewPropertyAnimator
import com.rndity.chatassistant.Size
import com.rndity.chatassistant.animator.ClearChatItemAnimator
import com.rndity.chatassistant.animator.AnimatorHandler
import com.rndity.chatassistant.animator.ViewPropertyAnimatorHandler

class SlideUpClearChatItemAnimator : ClearChatItemAnimator() {

    override fun animate(viewsToClear: List<View>, parentSize: Size): AnimatorHandler {
        val offset = viewsToClear.maxBy { view -> view.bottom }!!.bottom
        val result = mutableListOf<ViewPropertyAnimator>()
        for (view in viewsToClear) {
            result.add(view.animate().translationYBy(-offset.toFloat()).setDuration(defaultDuration()))
        }
        return ViewPropertyAnimatorHandler(result)
    }

}