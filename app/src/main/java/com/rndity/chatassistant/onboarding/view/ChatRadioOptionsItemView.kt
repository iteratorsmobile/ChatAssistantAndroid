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
import android.widget.LinearLayout
import com.dd.morphingbutton.MorphingButton
import com.eyalbira.loadingdots.LoadingDots
import com.rndity.chatassistant.Item
import com.rndity.chatassistant.R
import com.rndity.chatassistant.onboarding.RadioOption
import com.rndity.chatassistant.onboarding.RadioOptionsItem
import com.rndity.chatassistant.utils.AnimationConstant
import com.rndity.chatassistant.ItemMode
import kotlinx.android.synthetic.main.view_chat_radio_options_item.view.*

class ChatRadioOptionsItemView : ChatItemView {

    var optionClickListener: ((item: Item, option: RadioOption) -> Unit)? = null

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
        LayoutInflater.from(context).inflate(R.layout.view_chat_radio_options_item, this, true)
        setPadding(0, resources.getDimensionPixelOffset(R.dimen.offset_medium), resources.getDimensionPixelOffset(R.dimen.offset_medium), resources.getDimensionPixelOffset(R.dimen.offset_medium))
    }

    fun setItem(item: RadioOptionsItem, itemMode: ItemMode) {
        when (itemMode) {
            ItemMode.CURRENT_CONTEXT_ACTIVE -> {
                showOverlay = false
                createDotsAnimationAndHide(item)
            }
            ItemMode.CURRENT_CONTEXT_RESTORE -> {
                showOverlay = false
                configureWithoutAnimation(item)
            }
            ItemMode.PAST_CONTEXT_RESTORE -> {
                showOverlay = true
                configureWithoutAnimation(item)
            }
        }
    }

    private fun configureWithoutAnimation(item: RadioOptionsItem) {
        questionTextView.visibility = View.VISIBLE
        questionTextView.text = item.question

        for (idx in 1 until questionContainerView.childCount) {
            questionContainerView.removeViewAt(1)
        }

        for (option in item.options) {
            val morphingButton = MorphingButton(context).apply {
                layoutParams = LinearLayout.LayoutParams(resources.getDimension(R.dimen.rect_size_xsmall).toInt(),
                        resources.getDimension(R.dimen.rect_size_xsmall).toInt())

                setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_bg))
                setTextColor(ContextCompat.getColor(context, R.color.light_blue_text))
            }
            val params = morphingButton.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, resources.getDimension(R.dimen.offset_xsmall).toInt(), 0, 0)

            morphingButton.layoutParams = params

            post {
                val params2 = MorphingButton.Params.create()
                        .duration(0)
                        .width(questionButtonContainerView.width)
                        .height(resources.getDimension(R.dimen.rect_size_xsmall).toInt())
                        .cornerRadius(resources.getDimension(R.dimen.rect_corner).toInt())
                        .text(option.text)
                        .color(R.color.light_blue_bg)
                        .colorPressed(R.color.colorPrimary)
                morphingButton.morph(params2)
            }

            morphingButton.setOnClickListener { optionClickListener?.invoke(item, option) }
            questionButtonContainerView.addView(morphingButton)
        }
    }

    private fun createDotsAnimationAndHide(item: RadioOptionsItem) {
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

            questionTextView.text = item.question
            for (idx in 1 until questionContainerView.childCount) {
                questionContainerView.removeViewAt(1)
            }

            for (option in item.options) {
                val morphingButton = MorphingButton(context).apply {
                    layoutParams = LinearLayout.LayoutParams(resources.getDimension(R.dimen.rect_size_xsmall).toInt(),
                            resources.getDimension(R.dimen.rect_size_xsmall).toInt())

                    setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_bg))
                    setTextColor(ContextCompat.getColor(context, R.color.light_blue_text))
                }
                val params = morphingButton.layoutParams as LinearLayout.LayoutParams
                params.setMargins(0, resources.getDimension(R.dimen.offset_xsmall).toInt(), 0, 0)

                morphingButton.layoutParams = params

                postDelayed({
                    val params = MorphingButton.Params.create()
                            .duration(AnimationConstant.ANIMATION_DELEY)
                            //TODO: bug, layout can be not measured
                            .width(questionButtonContainerView.width)
                            .height(resources.getDimension(R.dimen.rect_size_xsmall).toInt())
                            .cornerRadius(resources.getDimension(R.dimen.rect_corner).toInt())
                            .text(option.text)
                            .color(R.color.light_blue_bg)
                            .colorPressed(R.color.colorPrimary)
                    morphingButton.morph(params)
                }, AnimationConstant.ANIMATION_DELEY.toLong())

                morphingButton.setOnClickListener { optionClickListener?.invoke(item, option) }
                questionButtonContainerView.addView(morphingButton)
            }
        }, AnimationConstant.ANIMATION_DURATION.toLong())
    }

}
