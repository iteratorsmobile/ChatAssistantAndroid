/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.rndity.chatassistant.*
import com.rndity.chatassistant.onboarding.view.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val keyFlowState = "flow_state"
    private lateinit var flow: OnBoardingChatFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatView.setViewAdapter(viewAdapter)
        chatView.flowListener = object : FlowListener {

            @SuppressLint("SetTextI18n")
            override fun onHistorySizeChanged(size: Int) {
                historySize.text = "History: $size"
            }

        }

        flow = OnBoardingChatFlow(chatView)
        val state = savedInstanceState?.getSerializable(keyFlowState) as? FlowPresenterState
        when (state) {
            null -> flow.start()
            else -> flow.restoreState(state)
        }

        prevHistoryView.setOnClickListener {
            val historySize = chatView.historySize()
            when (historySize) {
                0 -> {}
                else -> {
                    val currentPage = chatView.currentHistoryPage()
                    when (currentPage) {
                        null -> chatView.showHistoryPage(historySize - 1)
                        0 -> {}
                        else -> chatView.showHistoryPage(currentPage - 1)
                    }
                }
            }
        }

        nextHistoryView.setOnClickListener {
            val historySize = chatView.historySize()
            when (historySize) {
                0 -> {}
                else -> {
                    val currentPage = chatView.currentHistoryPage()
                    when (currentPage) {
                        null -> {}
                        historySize - 1 -> chatView.showActivePage()
                        else -> chatView.showHistoryPage(currentPage + 1)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable(keyFlowState, flow.getState())
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.windowToken?.let {
            inputMethodManager.hideSoftInputFromWindow(it, 0)
        }
    }

    private val viewAdapter = object : ViewAdapter {

        override fun viewForItem(item: Item, itemMode: ItemMode, parent: View): View {
            return when (item) {
                is QuestionHeaderItem -> ChatHeaderItemView(parent.context).apply {
                    setItem(item, itemMode)
                }
                is QuestionTextItem -> ChatTextItemView(parent.context).apply {
                    setItem(item, itemMode)
                }
                is TextEnterItem -> ChatTextEnterItemView(parent.context).apply {
                    setItem(item, itemMode)
                    textChangeListener = { _, text ->
                        hideKeyboard()
                        flow.proceedWithAnswerEntered(item, text ?: "")
                    }
                }
                is TextFixedItem -> ChatFixedTextItemView(parent.context).apply {
                    setItem(item, itemMode)
                }
                is ProgressItem -> ChatProgressItemView(parent.context)
                is RadioOptionsItem -> ChatRadioOptionsItemView(parent.context).apply {
                    setItem(item, itemMode)
                    optionClickListener = { itemId, option ->
                        hideKeyboard()
                        flow.proceedOptionSelected(itemId, option)
                    }
                }
                else -> throw IllegalStateException()
            }
        }
    }
}
