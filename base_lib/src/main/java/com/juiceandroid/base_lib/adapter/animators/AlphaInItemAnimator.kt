package com.juiceandroid.base_lib.adapter.animators

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.LinearInterpolator


class AlphaInItemAnimator(private val from: Float = 0f, private val duration: Long = 500L, private val interpolator: TimeInterpolator = LinearInterpolator()) :
    CustomItemAnimator {

    /**
     * 上划进入动画
     * @param v 显示动画View
     */
    override fun scrollUpAnim(v: View) {
        ObjectAnimator.ofFloat(v, "alpha", from, 1f)
                .setDuration(duration).apply {
                    interpolator = this@AlphaInItemAnimator.interpolator
                }
                .start()
    }

    /**
     * 下滑进入动画
     * @param v 显示动画View
     */
    override fun scrollDownAnim(v: View) {
        ObjectAnimator.ofFloat(v, "alpha", from, 1f)
                .setDuration(duration).apply {
                    interpolator = this@AlphaInItemAnimator.interpolator
                }
                .start()
    }

}