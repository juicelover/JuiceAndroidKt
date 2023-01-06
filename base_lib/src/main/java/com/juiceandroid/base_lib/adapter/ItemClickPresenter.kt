package com.juiceandroid.base_lib.adapter

import android.view.View

interface ItemClickPresenter<in Any> {
    /**
     * item点击处理
     * @param v 点击View
     * @param item 数据对象
     */
    fun onItemClick(v: View? = null, item: Any)
}