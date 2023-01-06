package com.juiceandroid.base_lib.tool

import android.os.CountDownTimer
import android.widget.TextView
import java.lang.ref.WeakReference


class CountDownTimerUtils(private val tv: TextView, val runTime: Long) {

    private var timer: CountDownTimer? = null

    var tvCodeWr: WeakReference<TextView>? = null

    /**
     * 启动倒计时
     */
    fun startCountDown() {
        tvCodeWr = WeakReference(tv)
        timer = object : CountDownTimer(runTime - 1, 1000) {
            override fun onFinish() {
                if (tvCodeWr?.get() != null) {
                    tvCodeWr?.get()?.text = "重新获取"
                    tvCodeWr?.get()?.isClickable = true
                    tvCodeWr?.get()?.isEnabled = true
                    tvCodeWr?.get()?.isSelected = true
                }
                finishCountDown()
            }

            override fun onTick(millisUntilFinished: Long) {
                if (tvCodeWr?.get() != null) {
                    tvCodeWr?.get()?.isClickable = false
                    tvCodeWr?.get()?.isEnabled = false
                    tvCodeWr?.get()?.isSelected = false
                    tvCodeWr?.get()?.text = "重新获取${millisUntilFinished / 1000}s"
                }
            }
        }.start()
    }

    /**
     * 这个方法需要在activity或者fragment销毁的时候调用，防止内存泄漏
     */
    fun finishCountDown() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }
}