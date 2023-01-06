package com.juiceandroid.base_lib.tool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import kotlinx.coroutines.*

object ToastTool {

    /** toast 常量 */
    private var lastShowTime = 0L
    private var lastShowHasMsg: String? = null
    private var curShowHasMsg: String? = null
    private const val TOAST_DURATION = 2000

    /**
     * 显示msg并在Log中打印出来
     * @param msg Toast内容，为空不显示
     * @param context Context对象
     * @param duration Toast持续时间
     */
    fun showToast(msg: String?, context: Context, duration: Int) {
        if (msg.isNullOrEmpty()) {
            return
        }
        val trace = Thread.currentThread().stackTrace
        val builder = StringBuilder()
        //获取索引位置
        var i = 0
        var flag = false
        while (i < trace.size) {
            if (flag) {
                if (trace[i].fileName != "ToastTool.kt") {
                    break
                }
            } else {
                if (trace[i].fileName == "ToastTool.kt") {
                    flag = true
                }
            }
            i++
        }
        builder.append("Toast提示内容：")
            .append(msg).append("\n")
        if (i != trace.size)
            builder.append("位置:(${trace[i].fileName}:${trace[i].lineNumber})")
        builder.toString().print("TOAST")
        show(msg, context, duration)
    }

    /**
     * 在主线程执行显示msg的操作
     * @param msg Toast内容，为空不显示
     * @param context Context对象
     * @param duration Toast持续时间
     */
    private fun show(msg: String, context: Context, duration: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val toast = Toast.makeText(context, msg, duration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    val toastClass = Toast::class.java
                    val mTN = toastClass.getDeclaredField("mTN")
                    mTN.isAccessible = true
                    val objTn = mTN[toast]
                    val clazzTn: Class<*> = objTn.javaClass
                    val mHandler = clazzTn.getDeclaredField("mHandler")
                    mHandler.isAccessible = true
                    mHandler[objTn] = HandlerProxy(mHandler[objTn] as Handler)
                } catch (e: Throwable) {
                }
            }
            toast.show()
        }
    }

    /**
     * 展示阅读器灰色背景toast
     * @param resId    资源id
     * @param title    字符串资源 不允许为空
     * @param gravity  toast 显示位置
     * @param duration toast 显示时间
     * */
    @SuppressLint("InflateParams", "SetTextI18n")
    fun showReadBgGrayToast(
        context: Context = ActivityMgr.getContext(),
        @DrawableRes resId: Int = 0,
        @NonNull title: String = "",
        gravity: Int = Gravity.CENTER,
        duration: Int = Toast.LENGTH_LONG
    ) {

    }

    /**
     * 展示自定义toast
     * @param context       上下文
     * @param toastRootView 自定义toast view
     * @param showMsg       显示文本
     * @param gravity       toast 显示位置
     * @param duration      toast 显示时间
     * */
    @Suppress("DEPRECATION")
    fun showCustomToast(
        context: Context,
        toastRootView: View? = null,
        @NonNull showMsg: String,
        gravity: Int = Gravity.CENTER,
        duration: Int = Toast.LENGTH_LONG
    ) {
        fun show() {
            CoroutineScope(Dispatchers.Main).launch {
                if (toastRootView == null) {
                    return@launch
                }
                delay(1000)
                //new toast
                val toast = Toast(context)
                toast.setGravity(gravity, 0, 20)
                toast.duration = duration
                toast.view = toastRootView
                toast.show()
            }
        }
        //防止重复点击
        curShowHasMsg = showMsg
        val curShowTime = System.currentTimeMillis()
        if (curShowHasMsg == lastShowHasMsg) {
            if (curShowTime - lastShowTime > TOAST_DURATION) {
                show()
                lastShowTime = curShowTime
                lastShowHasMsg = curShowHasMsg
            }
        } else {
            show()
            lastShowTime = curShowTime
            lastShowHasMsg = curShowHasMsg
        }
    }
}

/**
 * 扩展方法，String内容在Toast中显示
 * @receiver String Toast内容
 * @param context Context对象，默认为ActivityMgr.getContext()
 * @param duration Toast持续时间，默认使用短时间显示Toast.LENGTH_SHORT
 * @see Toast.LENGTH_SHORT
 */
fun String.showToast(
    context: Context = ActivityMgr.getContext(),
    duration: Int = Toast.LENGTH_SHORT,
    @DrawableRes resId: Int = 0
) {
    if (resId == 0) {
        ToastTool.showToast(this, context, duration)
    } else {
        ToastTool.showReadBgGrayToast(context, resId = resId, title = this)
    }

}