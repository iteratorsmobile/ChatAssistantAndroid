/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator.contextChange

import android.view.View
import com.rndity.chatassistant.Size
import com.rndity.chatassistant.animator.AnimatorHandler
import com.rndity.chatassistant.animator.ChainAnimationHandler
import com.rndity.chatassistant.animator.ContextChangeItemAnimator
import com.rndity.chatassistant.animator.ViewPropertyAnimatorHandler

class SlideTopContextChangeItemAnimator : ContextChangeItemAnimator() {

    override fun animate(viewsToClear: List<View>, viewsToShow: List<View>, parentSize: Size): AnimatorHandler {
        val offsetExit = viewsToClear.maxBy { view -> view.bottom }!!.bottom
        val offsetEnter = viewsToShow.maxBy { view -> view.bottom }!!.bottom

        for (viewToShow in viewsToShow) {
            viewToShow.translationY = -offsetEnter.toFloat()
        }

        val removePhase = {
            ViewPropertyAnimatorHandler(viewsToClear.map {
                it.animate().translationYBy(-offsetExit.toFloat()).setDuration(defaultDuration())
            })
        }

        val addPhase = {
            ViewPropertyAnimatorHandler(viewsToShow.map {
                it.animate().translationY(0.0f).setDuration(defaultDuration())
            })
        }

        return ChainAnimationHandler(listOf(removePhase, addPhase))
    }

}
