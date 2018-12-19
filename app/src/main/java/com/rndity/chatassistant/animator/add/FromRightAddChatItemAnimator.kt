/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.animator.add

import android.view.View
import android.view.ViewPropertyAnimator
import com.rndity.chatassistant.Size
import com.rndity.chatassistant.animator.AddChatItemAnimator
import com.rndity.chatassistant.animator.AnimatorHandler
import com.rndity.chatassistant.animator.ViewPropertyAnimatorHandler

class FromRightAddChatItemAnimator : AddChatItemAnimator() {

    override fun animate(views: List<View>, parentSize: Size): AnimatorHandler {
        val result = mutableListOf<ViewPropertyAnimator>()
        for (view in views) {
            view.translationX = view.width.toFloat()
            result.add(view.animate().translationX(0.0f).setDuration(defaultDuration()))
        }
        return ViewPropertyAnimatorHandler(result)
    }

}