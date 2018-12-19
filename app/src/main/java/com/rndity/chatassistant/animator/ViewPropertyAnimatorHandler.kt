/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator

import android.animation.Animator
import android.view.ViewPropertyAnimator
import com.rndity.chatassistant.utils.AnimatorListenerAdapter

class ViewPropertyAnimatorHandler(viewPropertyAnimators: List<ViewPropertyAnimator>) : AnimatorHandler {

    private var animationsCounter = viewPropertyAnimators.size

    private var _onDone: (() -> Unit)? = null
    override var onDone: (() -> Unit)?
        get() = _onDone
        set(value) {
            _onDone = value
        }

    private val proxyListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animator: Animator?) {
            if (--animationsCounter != 0) {
                return
            }
            onDone?.invoke()
        }
    }

    init {
        for (animator in viewPropertyAnimators) {
            animator.setListener(proxyListener)
            animator.start()
        }
    }
}