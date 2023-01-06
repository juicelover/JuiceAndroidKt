package com.juiceandroid.base_lib.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.juiceandroid.base_lib.tool.dp2px


class ItemDividerDecoration(
    private val horizontal: Int = 0,
    private val vertical: Int = 0,
    private val column: Int = 1
) : RecyclerView.ItemDecoration() {

    private val horizontalPx = horizontal.dp2px()
    private val verticalPx = vertical.dp2px()


    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        //水平总间距
        val totalHorizontalDivider = horizontalPx * (column + 1)
        //item水平间距
        val itemHorizontalDivider = totalHorizontalDivider / column
        //当前Item在List中的位置
        val listPosition = parent.getChildLayoutPosition(view)
        //当前Item在行中的位置
        val rowPosition = listPosition / column
        //当前Item在列中的位置
        val columnPosition = listPosition % column
        //item水平偏移值
        val itemHorizontalOffest = columnPosition*(horizontalPx-itemHorizontalDivider)+horizontalPx
        //设置边框
        outRect.apply {
            right = itemHorizontalDivider - itemHorizontalOffest
            top = if (listPosition < column) verticalPx else 0
            left = itemHorizontalOffest
            bottom = verticalPx
        }
    }
}