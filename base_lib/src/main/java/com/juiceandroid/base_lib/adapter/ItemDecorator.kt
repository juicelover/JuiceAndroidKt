package com.juiceandroid.base_lib.adapter

import androidx.databinding.ViewDataBinding

interface ItemDecorator{

    /**
     * 装饰item
     * @param holder ViewHolder
     * @param position item的位置
     * @param viewType item的viewType
     */
    fun decorator(holder: BindingViewHolder<ViewDataBinding>?, position: Int, viewType: Int)
}