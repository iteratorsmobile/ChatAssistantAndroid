/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator.contextChange

import android.view.View
import android.view.ViewPropertyAnimator
import com.rndity.chatassistant.Size
import com.rndity.chatassistant.animator.AnimatorHandler
import com.rndity.chatassistant.animator.ContextChangeItemAnimator
import com.rndity.chatassistant.animator.ViewPropertyAnimatorHandler

class SlideFromLeftContextChangeItemAnimator : ContextChangeItemAnimator() {

    override fun animate(viewsToClear: List<View>, viewsToShow: List<View>, parentSize: Size): AnimatorHandler {
        val result = mutableListOf<ViewPropertyAnimator>()
        for (view in viewsToClear) {
            result.add(view.animate()
                    .translationXBy(parentSize.width)
                    .setDuration(defaultDuration())
            )
        }
        for (view in viewsToShow) {
            view.translationX = -parentSize.width
            result.add(view.animate()
                    .translationX(0.0f)
                    .setDuration(defaultDuration())
            )
        }
        return ViewPropertyAnimatorHandler(result)
    }

}
