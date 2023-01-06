package com.juiceandroid.base_lib.adapter.animators

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.LinearInterpolator

class ScaleInItemAnimator(private val from: Float = .6f, private val duration: Long = 500L, private val interpolator: TimeInterpolator = LinearInterpolator()) :
    CustomItemAnimator {

    /**
     * 上划进入动画
     * @param v 显示动画View
     */
    override fun scrollUpAnim(v: View) {
        getAnimators(v).forEach {
            it.setDuration(duration).apply {
                interpolator = this@ScaleInItemAnimator.interpolator
            }.start()
        }
    }

    /**
     * 下滑进入动画
     * @param v 显示动画View
     */
    override fun scrollDownAnim(v: View) {
        getAnimators(v).forEach {
            it.setDuration(duration).apply {
                interpolator = this@ScaleInItemAnimator.interpolator
            }.start()
        }
    }

    /**
     * 获取动画队列
     * @param view 显示动画View
     * @return 动画队列
     */
    private fun getAnimators(view: View): Array<Animator> {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", from, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", from, 1f)
        return arrayOf(scaleX, scaleY)
    }


}