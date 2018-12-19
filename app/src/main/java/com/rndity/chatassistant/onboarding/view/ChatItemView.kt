/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

open class ChatItemView : RelativeLayout {

    private var overlayView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var showOverlay: Boolean
        get() = overlayView != null
        set(value) {
            when (value) {
                true -> addOverlay()
                false -> removeOverlay()
            }
        }

    private fun addOverlay() {
        if (overlayView != null) {
            return
        }
        overlayView = View(context)
        overlayView?.setBackgroundColor(Color.argb(128, 255, 255, 255))
        addView(overlayView)
    }

    private fun removeOverlay() {
        overlayView?.let {
            removeView(it)
            overlayView = null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        overlayView?.let {
            val widthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
            val heightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
            it.measure(widthSpec, heightSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        overlayView?.let {
            it.layout(0, 0, it.measuredWidth, it.measuredHeight)
        }
    }
}
