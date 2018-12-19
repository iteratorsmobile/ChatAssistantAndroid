/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator

class ChainAnimationHandler(phases: List<()->AnimatorHandler>) : AnimatorHandler {

    private var _onDone: (() -> Unit)? = null
    override var onDone: (() -> Unit)?
        get() = _onDone
        set(value) {
            _onDone = value
        }

    init {
        invoke(0, phases)
    }

    private fun invoke(idx: Int, phases: List<()->AnimatorHandler>) {
        phases[idx].invoke().onDone = {
            when (idx) {
                phases.size - 1 -> this.onDone?.invoke()
                else -> invoke(idx + 1, phases)
            }
        }
    }

}
