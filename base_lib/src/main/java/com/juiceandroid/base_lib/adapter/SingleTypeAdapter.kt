package com.juiceandroid.base_lib.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding

/**
 * @Date :2022/7/5
 * @Description:单ViewType式Adapter
 * @Author: 1244300796@qq.com
 */

class SingleTypeAdapter<T>(context: Context, private val layoutRes: Int, list: ObservableList<T>) : BaseAdapter<T>(context, list) {

    /**
     * 初始化，添加ListChanged监听，处理通知事件
     */
    init {
        list.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChanged(contributorViewModels: ObservableList<T>) {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(contributorViewModels: ObservableList<T>, i: Int, i1: Int) {
                notifyItemRangeChanged(i, i1)
            }

            override fun onItemRangeInserted(contributorViewModels: ObservableList<T>, i: Int, i1: Int) {
                notifyItemRangeInserted(i, i1)
            }

            override fun onItemRangeMoved(contributorViewModels: ObservableList<T>, i: Int, i1: Int, i2: Int) {
                notifyItemMoved(i, i1)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onItemRangeRemoved(contributorViewModels: ObservableList<T>, i: Int, i1: Int) {
                if (contributorViewModels.isEmpty()) {
                    mLastPosition = -1
                    notifyDataSetChanged()
                } else {
                    notifyItemRangeRemoved(i, i1)
                }
            }

        })
    }

    /**
     * 生成ViewHolder
     * @param parent 父视图
     * @param viewType viewType
     * @return 生成的ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BindingViewHolder(DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutRes, parent, false))


}