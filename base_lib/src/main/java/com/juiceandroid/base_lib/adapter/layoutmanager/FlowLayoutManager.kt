package com.juiceandroid.base_lib.adapter.layoutmanager

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class FlowLayoutManager : RecyclerView.LayoutManager(){

    private val self = this

    private var left: Int = 0
    private var top:Int = 0
    private var right:Int = 0
    //最大容器的宽度
    private var usedMaxWidth: Int = 0
    //竖直方向上的偏移量
    private var verticalScrollOffset = 0

    var maxLineNumber: Int? = null

    //计算显示的内容的高度
    var totalHeight = 0
    private var row = Row()
    private val lineRows = mutableListOf<Row>()

    //保存所有的Item的上下左右的偏移量信息
    private val allItemFrames = SparseArray<Rect>()


    override fun isAutoMeasureEnabled(): Boolean {
        //设置主动测量规则,适应recyclerView高度为wrap_content
        return true
    }

    /**
     * 每个item的定义
     */
    inner class Item(internal var useHeight: Int, internal var view: View, internal var rect: Rect) {

        fun setRect(rect: Rect) {
            this.rect = rect
        }
    }

    /**
     * 行信息的定义
     */
    inner class Row {

        //每一行的头部坐标
        var cuTop: Float = 0.0f
        //每一行需要占据的最大高度
        var maxHeight: Float = 0.0f
        //每一行存储的item
        internal var views: MutableList<Item> = ArrayList()

        fun addViews(view: Item) {
            views.add(view)
        }
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * 用来获取每一个item在屏幕上占据的位置
     * @param recycler RecyclerView
     * @param state RecyclerView状态
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler!!)
            verticalScrollOffset = 0
            return
        }
        if (childCount == 0 && state!!.isPreLayout) {
            return
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler!!)
        if (childCount == 0) {
            left = paddingLeft
            right = paddingRight
            top = paddingTop
            usedMaxWidth = width - left - right
        }
        totalHeight = 0
        var cuLineTop = top
        //当前行使用的宽度
        var cuLineWidth = 0
        var itemLeft: Int
        var itemTop: Int
        var maxHeightItem = 0
        row = Row()
        lineRows.clear()
        allItemFrames.clear()
        removeAllViews()
        var maxLine = this.maxLineNumber
        if (maxLine != null) {
            maxLine--
        }
        for (i in 0 until itemCount) {
            val childAt = recycler.getViewForPosition(i)
            if (View.GONE == childAt.visibility) {
                continue
            }
            measureChildWithMargins(childAt, 0, 0)
            val childWidth = getDecoratedMeasuredWidth(childAt)
            val childHeight = getDecoratedMeasuredHeight(childAt)
            //如果加上当前的item还小于最大的宽度的话
            if (cuLineWidth + childWidth <= usedMaxWidth) {
                itemLeft = left + cuLineWidth
                itemTop = cuLineTop
                var frame: Rect? = allItemFrames.get(i)
                if (frame == null) {
                    frame = Rect()
                }
                frame.set(itemLeft, itemTop, itemLeft + childWidth, itemTop + childHeight)
                allItemFrames.put(i, frame)
                cuLineWidth += childWidth
                maxHeightItem = maxHeightItem.coerceAtLeast(childHeight)
                row.addViews(Item(childHeight, childAt, frame))
                row.cuTop=cuLineTop.toFloat()
                row.maxHeight=maxHeightItem.toFloat()
            } else {
                if (maxLine != null) {
                    maxLine--
                }
                if (maxLine == null || maxLine >= 0) {
                    //换行
                    formatAboveRow()
                    cuLineTop += maxHeightItem
                    totalHeight += maxHeightItem
                    itemTop = cuLineTop
                    itemLeft = left
                    var frame: Rect? = allItemFrames.get(i)
                    if (frame == null) {
                        frame = Rect()
                    }
                    frame!!.set(itemLeft, itemTop, itemLeft + childWidth, itemTop + childHeight)
                    allItemFrames.put(i, frame)
                    cuLineWidth = childWidth
                    maxHeightItem = childHeight
                    row.addViews(Item(childHeight, childAt, frame))
                    row.cuTop=cuLineTop.toFloat()
                    row.maxHeight=maxHeightItem.toFloat()
                } else {
                    formatAboveRow()
                    totalHeight += maxHeightItem
                    break
                }

            }
            //不要忘了最后一行进行刷新下布局
            if (i == itemCount - 1) {
                formatAboveRow()
                totalHeight += maxHeightItem
            }

        }
        totalHeight = totalHeight.coerceAtLeast(getVerticalSpace())
        fillLayout(recycler, state!!)
    }

    /**
     * 对出现在屏幕上的item进行展示，超出屏幕的item回收到缓存中
     * @param recycler RecyclerView
     * @param state RecyclerView状态
     */
    private fun fillLayout(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        if (state.isPreLayout) { // 跳过preLayout，preLayout主要用于支持动画
            return
        }
        // 当前scroll offset状态下的显示区域
        val displayFrame = Rect(paddingLeft, paddingTop + verticalScrollOffset,
            width - paddingRight, verticalScrollOffset + (height - paddingBottom))

        //对所有的行信息进行遍历
        for (j in lineRows.indices) {
            val row = lineRows[j]
            val lineTop = row.cuTop
            val lineBottom = lineTop + row.maxHeight
            //如果该行在屏幕中，进行放置item
            if (lineTop < displayFrame.bottom && displayFrame.top < lineBottom) {
                val views = row.views
                for (i in views.indices) {
                    val scrap = views[i].view
                    measureChildWithMargins(scrap, 0, 0)
                    addView(scrap)
                    val frame = views[i].rect
                    //将这个item布局出来
                    layoutDecoratedWithMargins(scrap,
                        frame.left,
                        frame.top - verticalScrollOffset,
                        frame.right,
                        frame.bottom - verticalScrollOffset)
                }
            } else {
                //将不在屏幕中的item放到缓存中
                val views = row.views
                for (i in views.indices) {
                    val scrap = views[i].view
                    removeAndRecycleView(scrap, recycler!!)
                }
            }
        }
    }

    /**
     * 计算每一行没有居中的viewgroup，让居中显示
     */
    private fun formatAboveRow() {
        val views = row.views
        for (i in views.indices) {
            val item = views[i]
            val view = item.view
            val position = getPosition(view)
            //如果该item的位置不在该行中间位置的话，进行重新放置
            if (allItemFrames.get(position).top < row.cuTop + (row.maxHeight - views[i].useHeight) / 2) {
                var frame: Rect? = allItemFrames.get(position)
                if (frame == null) {
                    frame = Rect()
                }
                frame.set(allItemFrames.get(position).left, (row.cuTop + (row.maxHeight - views[i].useHeight) / 2).toInt(),
                    allItemFrames.get(position).right, (row.cuTop + (row.maxHeight - views[i].useHeight) / 2 + getDecoratedMeasuredHeight(view).toFloat()).toInt())
                allItemFrames.put(position, frame)
                item.setRect(frame)
                views[i] = item
            }
        }
        row.views = views
        lineRows.add(row)
        row = Row()
    }

    /**
     * 竖直方向需要滑动的条件
     */
    override fun canScrollVertically(): Boolean {
        return true
    }

    /**
     * 监听竖直方向滑动的偏移量
     * @param dy 滑动的距离
     * @param recycler RecyclerView
     * @param state RecyclerView状态
     * @return Int
     */
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?,
                                    state: RecyclerView.State?): Int {
        //实际要滑动的距离
        var travel = dy

        //如果滑动到最顶部
        if (verticalScrollOffset + dy < 0) {//限制滑动到顶部之后，不让继续向上滑动了
            travel = -verticalScrollOffset//verticalScrollOffset=0
        } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
            travel = totalHeight - getVerticalSpace() - verticalScrollOffset//verticalScrollOffset=totalHeight - getVerticalSpace()
        }

        //将竖直方向的偏移量+travel
        verticalScrollOffset += travel

        // 平移容器内的item
        offsetChildrenVertical(-travel)
        fillLayout(recycler, state!!)
        return travel
    }

    /**
     * 获取垂直间隙
     * @return 垂直间隙
     */
    private fun getVerticalSpace(): Int {
        return self.height - self.paddingBottom - self.paddingTop
    }

    /**
     * 获取水平间隙
     * @return 水平间隙
     */
    fun getHorizontalSpace(): Int {
        return self.width - self.paddingLeft - self.paddingRight
    }
}