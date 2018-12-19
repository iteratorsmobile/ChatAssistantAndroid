/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.rndity.chatassistant.animator.*
import com.rndity.chatassistant.utils.split

open class ChatView : ViewGroup, FlowPresenter {

    private val tag = "ChatView"

    private var _flowListener: FlowListener? = null

    override var flowListener: FlowListener?
        get() = _flowListener
        set(value) {
            _flowListener = value
        }

    private val history = mutableListOf<ItemsContext>()
    private var activeContext: ItemsContext? = null

    private var viewAdapter: ViewAdapter? = null

    private var rows = mutableListOf<RowData>()
    //used when context switching
    private var rowsUpcoming: MutableList<RowData>? = null

    private var pendingExecutions = mutableListOf<() -> Boolean>()

    private var currentHistoryIndex: Int? = null

    constructor(context: Context) : super(context) {
        initWithAttributes(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initWithAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initWithAttributes(attrs)
    }

    private fun initWithAttributes(attrs: AttributeSet?) {

    }

    override fun getState(): FlowPresenterState {
        val pages = history.toMutableList()
        val activePage: Int
        val activeContext = this.activeContext
        val historyIndex = this.currentHistoryIndex
        when (activeContext) {
            null -> {
                pages.add(ItemsContext(rows.map { rowData -> rowData.item }))
                activePage = pages.size - 1
            }
            else -> {
                //note: active context buffered so currently displaying history page
                pages.add(activeContext)
                activePage = historyIndex!!
            }
        }
        return FlowPresenterState(pages, activePage)
    }

    override fun restoreState(state: FlowPresenterState) {
        history.clear()
        activeContext = null
        rows.clear()
        rowsUpcoming = null
        pendingExecutions.clear()
        currentHistoryIndex = null
        removeAllViews()

        val pages = state.pages
        val pagesCount = pages.size
        when (pagesCount) {
            0 -> { }
            1 -> {
                //note: no history
                for (item in pages.first().items) {
                    rows.add(RowData(createViewForItem(item, ItemMode.CURRENT_CONTEXT_RESTORE), item))
                }
            }
            else -> {
                history.addAll(MutableList(pagesCount - 1, { pages[it] }))
                if (state.activePage == pagesCount - 1) {
                    //note: restoring active
                    for (item in pages.last().items) {
                        rows.add(RowData(createViewForItem(item, ItemMode.CURRENT_CONTEXT_RESTORE), item))
                    }
                } else {
                    //note: restoring history
                    currentHistoryIndex = state.activePage
                    activeContext = pages.last()

                    for (item in pages[state.activePage].items) {
                        rows.add(RowData(createViewForItem(item, ItemMode.PAST_CONTEXT_RESTORE), item))
                    }
                }
            }
        }
    }

    override fun setItems(vararg items: Item) {
        Log.v(tag, "setItems(): count:${items.size}")

        removeAllViews()
        for (item in items) {
            rows.add(RowData(createViewForItem(item, ItemMode.CURRENT_CONTEXT_RESTORE), item))
        }
    }

    override fun addItems(vararg items: Item, delay: Long, animator: AddChatItemAnimator?, action: PendingAction?) {
        Log.v(tag, "addItems(): count:${items.size}, has animator:${animator != null}, has action:${action != null}")

        execute(delay) {
            val newRows = mutableListOf<RowData>()
            for (item in items) {
                newRows.add(RowData(createViewForItem(item, ItemMode.CURRENT_CONTEXT_ACTIVE), item))
            }
            rows.addAll(newRows)

            when (animator) {
                null -> {
                    runPendingAction(action)
                }
                else -> {
                    val views = newRows.map { rowData -> rowData.view }
                    val pendingExecution = {
                        animator.animate(views, Size(width.toFloat(), height.toFloat())).onDone = {
                            animator.onDone(views)
                            runPendingAction(action)
                        }
                        true
                    }
                    schedulePendingExecution(pendingExecution)
                }
            }
        }
    }

    override fun replaceItem(oldItem: Item, newItem: Item, delay: Long, animator: ReplaceChatItemAnimator?, action: PendingAction?) {
        Log.v(tag, "replaceItem(): oldItem:$oldItem, newItem:$newItem, has animator:${animator != null}, has action:${action != null}")

        execute(delay) {
            val index = rows.indexOfFirst { rowData -> rowData.item.id == oldItem.id }.takeIf { it >= 0 }
                    ?: throw IllegalStateException("item not found")
            when (animator) {
                null -> {
                    removeView(rows[index].view)
                    rows[index] = RowData(createViewForItem(newItem, ItemMode.CURRENT_CONTEXT_ACTIVE), newItem)

                    runPendingAction(action)
                }
                else -> {
                    val temporaryRowData = PendingReplaceRowData(rows[index].view, createViewForItem(newItem, ItemMode.CURRENT_CONTEXT_ACTIVE), newItem)
                    rows[index] = temporaryRowData

                    val pendingExecution = {
                        animator.animate(temporaryRowData.newView, temporaryRowData.viewToReplace, Size(width.toFloat(), height.toFloat())).onDone = {
                            animator.onDone(temporaryRowData.newView, temporaryRowData.viewToReplace)
                            removeView(temporaryRowData.viewToReplace)
                            rows[index] = RowData(temporaryRowData.newView, newItem)
                            runPendingAction(action)
                        }
                        true
                    }
                    schedulePendingExecution(pendingExecution)
                }
            }
        }
    }

    override fun replaceLastItem(newItem: Item, delay: Long, animator: ReplaceChatItemAnimator?, action: PendingAction?) {
        replaceItem(rows.last().item, newItem, delay, animator, action)
    }

    override fun clearItems(moveToHistory: Boolean, delay: Long, animator: ClearChatItemAnimator?, action: PendingAction?) {
        Log.v(tag, "clearItems(): move to history:$moveToHistory, has animator:${animator != null}, has action:${action != null}")

        execute(delay) {
            if (moveToHistory) {
                history.add(ItemsContext(rows.map { it.item }))
                flowListener?.let {
                    post { it.onHistorySizeChanged(history.size) }
                }
            }

            val removeAction = {
                rows.clear()
                removeAllViews()
            }

            when (animator) {
                null -> {
                    removeAction.invoke()
                    runPendingAction(action)
                }
                else -> {
                    val views = rows.map { rowData -> rowData.view }
                    val pendingExecution = {
                        animator.animate(views, Size(width.toFloat(), height.toFloat())).onDone = {
                            animator.onDone(views)
                            removeAction.invoke()
                            runPendingAction(action)
                        }
                        true
                    }
                    schedulePendingExecution(pendingExecution)
                }
            }
        }
    }

    override fun clearToItem(item: Item, moveToHistory: Boolean, includeItemInHistory: Boolean, delay: Long, animator: KeepChatItemAnimator?, action: PendingAction?) {
        Log.v(tag, "clearToItem(): moveToHistory:$moveToHistory, includeItemInHistory:$includeItemInHistory, item:$item, has action:${action != null}")

        execute(delay) {
            val rowIdx = rows.indexOfFirst { it -> it.item.id == item.id }.takeIf { it >= 0 }
                    ?: throw IllegalStateException("item not found")

            if (moveToHistory) {
                history.add(ItemsContext(rows.subList(0, rowIdx + if (includeItemInHistory) 1 else 0).map { rowData -> rowData.item }))
                flowListener?.let {
                    post { it.onHistorySizeChanged(history.size) }
                }
            }

            val removeAction = {
                for (idx in 0 until rowIdx) {
                    removeView(rows[0].view)
                    rows.removeAt(0)
                }
            }

            when (animator) {
                null -> {
                    removeAction.invoke()
                    runPendingAction(action)
                }
                else -> {
                    val pendingExecution = {
                        val views = rows.map { rowData -> rowData.view }.split(rowIdx)
                        animator.animate(views.first, views.second, Size(width.toFloat(), height.toFloat())).onDone = {
                            animator.onDone(views.first, views.second)
                            removeAction.invoke()
                            runPendingAction(action)
                        }
                        true
                    }
                    schedulePendingExecution(pendingExecution)
                }
            }
        }
    }

    override fun keepsLastItems(count: Int, moveToHistory: Boolean, includeItemsInHistory: Boolean, delay: Long, animator: KeepChatItemAnimator?, action: PendingAction?) {
        Log.v(tag, "keepsLastItems(): moveToHistory:$moveToHistory, includeItemsInHistory:$includeItemsInHistory, count:$count, total:${rows.size}, has animator:${animator != null}, has action:${action != null}")

        clearToItem(rows[rows.size - count].item, moveToHistory, includeItemsInHistory, delay, animator, action)
    }

    override fun historySize() = history.size

    override fun showHistoryPage(pageIndex: Int, delay: Long, animator: ContextChangeItemAnimator?, action: PendingAction?) {
        execute(delay) {
            val historyIndex = if (pageIndex < historySize()) pageIndex else throw IllegalArgumentException("invalid pageIndex")
            currentHistoryIndex = historyIndex

            if (activeContext == null) {
                activeContext = ItemsContext(rows.map { rowData -> rowData.item })
            }

            replaceItemsContext(history[historyIndex], true, animator, action)
        }
    }

    override fun showActivePage(delay: Long, animator: ContextChangeItemAnimator?, action: PendingAction?) {
        execute(delay) {
            currentHistoryIndex = null

            val itemsContext = activeContext ?: throw IllegalStateException("no context saved")

            replaceItemsContext(itemsContext, false, animator, action)
        }
    }

    private fun replaceItemsContext(itemsContext: ItemsContext, isHistoryContext: Boolean, animator: ContextChangeItemAnimator?, action: PendingAction?) {
        val itemMode = if (isHistoryContext) ItemMode.PAST_CONTEXT_RESTORE else ItemMode.CURRENT_CONTEXT_RESTORE
        when (animator) {
            null -> {
                rows.clear()
                removeAllViews()

                for (item in itemsContext.items) {
                    rows.add(RowData(createViewForItem(item, itemMode), item))
                }

                runPendingAction(action)
            }
            else -> {
                val newRows = mutableListOf<RowData>()
                rowsUpcoming = newRows

                for (item in itemsContext.items) {
                    newRows.add(RowData(createViewForItem(item, itemMode), item))
                }

                val pendingExecution = {
                    val viewsToClear = rows.map { rowData -> rowData.view }
                    val viewsToShow = newRows.map { rowData -> rowData.view }
                    animator.animate(viewsToClear, viewsToShow, Size(width.toFloat(), height.toFloat())).onDone = {
                        animator.onDone(viewsToClear, viewsToShow)

                        for (row in rows) {
                            removeView(row.view)
                        }
                        rows = newRows
                        rowsUpcoming = null

                        runPendingAction(action)
                    }
                    true
                }
                schedulePendingExecution(pendingExecution)
            }
        }
    }

    override fun currentHistoryPage() = currentHistoryIndex

    fun setViewAdapter(viewAdapter: ViewAdapter) {
        if (viewAdapter == this.viewAdapter) {
            return
        }
        this.viewAdapter = viewAdapter
        removeAllViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        var availableHeight: Long = MeasureSpec.getSize(heightMeasureSpec).toLong()

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            availableHeight = Int.MAX_VALUE.toLong()
        }

        val childModeWidth = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)
        val childModeHeight = MeasureSpec.makeMeasureSpec(availableHeight.toInt(), MeasureSpec.UNSPECIFIED)

        var consumedHeightByRows = 0
        for (row in rows) {
            consumedHeightByRows += when (row) {
                is PendingReplaceRowData -> {
                    row.newView.measure(childModeWidth, childModeHeight)
                    row.viewToReplace.measure(childModeWidth, childModeHeight)
                    Math.max(row.newView.measuredHeight, row.viewToReplace.measuredHeight)
                }
                else -> {
                    row.view.measure(childModeWidth, childModeHeight)
                    row.view.measuredHeight
                }
            }
            if (consumedHeightByRows >= availableHeight) {
                break
            }
        }

        var consumedHeightByRowsUpcoming = 0
        rowsUpcoming?.let {
            for (row in it) {
                row.view.measure(childModeWidth, childModeHeight)
                consumedHeightByRowsUpcoming += row.view.measuredHeight
                if (consumedHeightByRowsUpcoming >= availableHeight) {
                    break
                }
            }
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(availableWidth, Math.max(consumedHeightByRows, consumedHeightByRowsUpcoming))
        } else {
            setMeasuredDimension(availableWidth, availableHeight.toInt())
        }
    }

    private fun schedulePendingExecution(pendingExecution: () -> Boolean) {
        Log.v(tag, "schedulePendingExecution()")

        pendingExecutions.add(pendingExecution)

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnPreDrawListener(preDrawListener)
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var cursorY = 0
        for (row in rows) {
            cursorY += when (row) {
                is PendingReplaceRowData -> {
                    row.newView.layout(0, cursorY, row.view.measuredWidth, cursorY + row.view.measuredHeight)
                    row.viewToReplace.layout(0, cursorY, row.viewToReplace.measuredWidth, cursorY + row.viewToReplace.measuredHeight)
                    Math.max(row.newView.measuredHeight, row.viewToReplace.measuredHeight)
                }
                else -> {
                    row.view.layout(0, cursorY, row.view.measuredWidth, cursorY + row.view.measuredHeight)
                    row.view.measuredHeight
                }
            }
        }

        rowsUpcoming?.let {
            cursorY = 0
            for (row in it) {
                row.view.layout(0, cursorY, row.view.measuredWidth, cursorY + row.view.measuredHeight)
                cursorY += row.view.measuredHeight
            }
        }
    }

    private fun createViewForItem(item: Item, itemMode: ItemMode): View {
        val view = viewAdapter!!.viewForItem(item, itemMode, this)
        addView(view)
        return view
    }

    private val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {

        override fun onPreDraw(): Boolean {
            Log.v(tag, "onPreDraw()")

            viewTreeObserver?.removeOnPreDrawListener(this)

            var result = true

            for (pendingExecution in pendingExecutions) {
                val continueWithOnDraw = pendingExecution.invoke()
                if (!continueWithOnDraw) {
                    result = false
                }
            }
            pendingExecutions.clear()

            return result
        }
    }

    private fun runPendingAction(pendingAction: PendingAction?) {
        Log.v(tag, "runPendingAction(): has action:${pendingAction != null}")
        val action = pendingAction ?: return
        post {
            action.invoke()
        }
    }

    private fun execute(delay: Long, action: () -> Unit) {
        when (delay) {
            0L -> action.invoke()
            else -> postDelayed(action, delay)
        }
    }

    private open class RowData(val view: View, val item: Item)
    private class PendingReplaceRowData(val viewToReplace: View, val newView: View, val newItem: Item) : RowData(newView, newItem)
}
