/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant

abstract class ChatFlow(protected val presenter: FlowPresenter) {
    abstract fun start()

    //note: probably some filtering required for active context in extending classes
    fun getState(): FlowPresenterState
        = presenter.getState()

    fun restoreState(state: FlowPresenterState)
        = presenter.restoreState(state)

}