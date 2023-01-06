package com.juiceandroid.base_lib.base

import android.app.Activity
import android.view.View
import com.gyf.immersionbar.ImmersionBar


interface Presenter : View.OnClickListener {

    /**
     * 点击事件
     * @param v View
     */
    override fun onClick(v: View?)

    fun baseApplySkin(activity: Activity)

    /**
     * 设置状态栏
     */
    fun setStatusBar(bar: ImmersionBar)

    /**
     * 加载数据
     * @param isRefresh 是否是刷新。一般为true。分页加载时使用（第一次加载：true  加载更多：false）。
     */
    fun loadData(isRefresh: Boolean)

}