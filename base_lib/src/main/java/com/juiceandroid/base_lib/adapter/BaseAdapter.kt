package com.juiceandroid.base_lib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.juiceandroid.base_lib.BR
import com.juiceandroid.base_lib.adapter.animators.CustomItemAnimator
import com.juiceandroid.base_lib.adapter.animators.ScaleInItemAnimator


/**
 * RecyclerView的Adapter基类
 * @date 2021/2/25
 */
abstract class BaseAdapter<T>(context: Context, private val list: ObservableList<T>) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    protected val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    var itemPresenter: ItemClickPresenter<T>? = null

    var itemDecorator: ItemDecorator? = null

    var itemAnimator: CustomItemAnimator? = ScaleInItemAnimator(interpolator = DecelerateInterpolator())

    var mLastPosition = -1
    var isFirstOnly = false
    var showItemAnimator = false

    /**
     * 绑定ViewHolder
     * @param holder ViewHolder
     * @param position 位置
     */
    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        val item = list[position]
        holder.binding.setVariable(BR.item, item)
        holder.binding.setVariable(BR.presenter, itemPresenter)
        holder.binding.executePendingBindings()
        itemDecorator?.decorator(holder, position, getItemViewType(position))

        itemAnimator?.let {
            if (!showItemAnimator) {
                return@let
            }
            val adapterPosition = holder.adapterPosition
            if (!isFirstOnly || adapterPosition > mLastPosition) {
                if (adapterPosition >= mLastPosition) {
                    it.scrollDownAnim(holder.binding.root)
                } else {
                    it.scrollUpAnim(holder.binding.root)
                }
                mLastPosition = adapterPosition
            } else {
                clear(holder.binding.root)
            }
        }
    }

    /**
     * 获取Item数量
     * @return Item数量
     */
    override fun getItemCount(): Int = list.size

    /**
     * 获取Item
     * @param position 位置
     * @return Item
     */
    fun getItem(position: Int): T? = list[position]


    /**
     * 清除动画
     * @param v View
     */
    private fun clear(v: View) {
        v.alpha = 1f
        v.scaleY = 1f
        v.scaleX = 1f
        v.translationY = 0f
        v.translationX = 0f
        v.rotation = 0f
        v.rotationX = 0f
        v.rotationY = 0f
        v.pivotX = v.measuredWidth.toFloat() / 2
        v.pivotY = v.measuredHeight.toFloat() / 2
        v.animate().setInterpolator(null).startDelay = 0
    }
}