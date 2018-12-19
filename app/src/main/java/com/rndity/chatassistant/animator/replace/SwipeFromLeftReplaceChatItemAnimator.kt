/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator.replace

import android.view.View
import android.view.ViewPropertyAnimator
import com.rndity.chatassistant.Size
import com.rndity.chatassistant.animator.ReplaceChatItemAnimator
import com.rndity.chatassistant.animator.AnimatorHandler
import com.rndity.chatassistant.animator.ViewPropertyAnimatorHandler

class SwipeFromLeftReplaceChatItemAnimator : ReplaceChatItemAnimator() {

    override fun animate(newView: View, oldView: View, parentSize: Size): AnimatorHandler {
        val result = mutableListOf<ViewPropertyAnimator>()

        oldView.translationX = 0.0f
        result.add(oldView.animate().translationX(oldView.width.toFloat()).setDuration(defaultDuration()))

        newView.translationX = -newView.width.toFloat()
        result.add(newView.animate().translationX(0.0f).setDuration(defaultDuration()))

        return ViewPropertyAnimatorHandler(result)
    }

}