/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding.view

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.eyalbira.loadingdots.LoadingDots
import com.rndity.chatassistant.R
import com.rndity.chatassistant.onboarding.QuestionHeaderItem
import com.rndity.chatassistant.utils.AnimationConstant
import com.rndity.chatassistant.ItemMode
import kotlinx.android.synthetic.main.view_chat_header_item.view.*

class ChatHeaderItemView : ChatItemView {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.view_chat_header_item, this, true)

        resources.getDimensionPixelOffset(R.dimen.offset_medium).let {
            setPadding(0, it, it, it)
        }
    }

    fun setItem(item: QuestionHeaderItem, itemMode: ItemMode) {
        questionTextView.text = item.text

        when (itemMode) {
            ItemMode.CURRENT_CONTEXT_ACTIVE -> {
                showOverlay = false
                createDotsAnimationAndHide()
            }
            ItemMode.CURRENT_CONTEXT_RESTORE -> {
                showOverlay = false
                questionTextView.visibility = View.VISIBLE
            }
            ItemMode.PAST_CONTEXT_RESTORE -> {
                showOverlay = true
                questionTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun createDotsAnimationAndHide() {
        val loadingDots = LoadingDots(context)

        loadingDots.setJumpDuraiton(AnimationConstant.JUMP_DURATION)
        loadingDots.loopDuration = AnimationConstant.LOOP_DURATION
        loadingDots.loopStartDelay = AnimationConstant.LOOP_START_DELAY
        loadingDots.dotsSpace = AnimationConstant.DOTS_SPACE
        loadingDots.dotsSize = AnimationConstant.DOTS_SIZE
        loadingDots.dotsColor = ContextCompat.getColor(context, R.color.light_blue_text)
        questionContainerView.addView(loadingDots)
        loadingDots.startAnimation()

        postDelayed({
            loadingDots.stopAnimation()
            loadingDots.visibility = View.GONE
            questionTextView.visibility = View.VISIBLE
        }, AnimationConstant.ANIMATION_DURATION.toLong())
    }

}
