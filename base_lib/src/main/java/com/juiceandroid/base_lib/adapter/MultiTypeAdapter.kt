package com.juiceandroid.base_lib.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.collection.ArrayMap
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @Date :2022/7/5
 * @Description:多ViewType式Adapter
 * @Author: 1244300796@qq.com
 */

class MultiTypeAdapter(context: Context, list: ObservableArrayList<Any>, val multiViewTyper: MultiViewTyper) : BaseAdapter<Any>(context, list) {

    /**
     * item元素对应ViewType的集合
     */
    protected var mCollectionViewType: MutableList<Int> = mutableListOf()
    /**
     * itemType和Map对应关系的Map
     */
    private val mItemTypeToLayoutMap = ArrayMap<Int, Int>()
    /**
     * itemType和span对应关系的Map
     */
    private val mItemTypeToSpanMap = ArrayMap<Int, Int>()

    /**
     * 初始化，添加ListChanged监听，处理通知事件
     */
    init {
        list.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<Any>>() {
            override fun onItemRangeMoved(sender: ObservableList<Any>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeRemoved(sender: ObservableList<Any>?, positionStart: Int, itemCount: Int) {
                for (i in positionStart + itemCount - 1 downTo positionStart) mCollectionViewType.removeAt(i)
                if (sender?.isNotEmpty() == true) {
                    notifyItemRangeRemoved(positionStart, itemCount)
                } else {
                    mLastPosition = -1
                    notifyDataSetChanged()
                }
            }

            override fun onItemRangeChanged(sender: ObservableList<Any>?, positionStart: Int, itemCount: Int) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(sender: ObservableList<Any>?, positionStart: Int, itemCount: Int) {
                sender?.run {
                    (positionStart until positionStart + itemCount).forEach {
                        mCollectionViewType.add(it, multiViewTyper.getViewType(this[it]))
                    }
                    notifyItemRangeInserted(positionStart, itemCount)
                }
            }

            override fun onChanged(sender: ObservableList<Any>?) {
                notifyDataSetChanged()
            }

        })
    }

    /**
     * 生成ViewHolder
     * @param parent 父视图
     * @param viewType viewType
     * @return 生成的ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
        return BindingViewHolder(
            DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, getLayoutRes(viewType), parent, false))
    }

    /**
     * 添加viewType和布局的关系
     * @param viewType viewType
     * @param layoutRes 页面布局资源
     * @param span item的span，默认为1
     */
    fun addViewTypeToLayoutMap(viewType: Int?, layoutRes: Int?, span: Int=1) {
        mItemTypeToLayoutMap[viewType] = layoutRes
        mItemTypeToSpanMap[viewType] = span
    }

    /**
     * 获取指定位置的viewType
     * @param position 位置
     * @return viewType
     */
    override fun getItemViewType(position: Int): Int = mCollectionViewType[position]

    interface MultiViewTyper {
        fun getViewType(item: Any): Int
    }

    /**
     * 获取页面布局资源
     * @param viewType 位置
     * @return 页面布局资源
     */
    @LayoutRes
    protected fun getLayoutRes(viewType: Int): Int = mItemTypeToLayoutMap[viewType]!!

    /**
     * 设置不同viewType的span
     * @param recyclerView RecyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return mItemTypeToSpanMap[getItemViewType(position)]?:1
                }
            }
        }
    }
}