package com.juiceandroid.base_lib.adapter.animators

import android.view.View

interface CustomItemAnimator{

    /**
     * 上划进入动画
     * @param v 显示动画View
     */
    fun scrollUpAnim(v: View)

    /**
     * 下滑进入动画
     * @param v 显示动画View
     */
    fun scrollDownAnim(v: View)
}