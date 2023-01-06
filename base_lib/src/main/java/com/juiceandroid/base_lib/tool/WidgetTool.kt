package com.juiceandroid.base_lib.tool

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

object WidgetTool {

    /**
     * 获取屏幕宽度
     * @param context 上下文对象
     * @return 屏幕宽度，单位PX
     */
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(mDisplayMetrics)
        } else {
            wm.defaultDisplay.getMetrics(mDisplayMetrics)
        }
        return mDisplayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     * @param context 上下文对象
     * @return 屏幕高度，单位PX
     */
    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(mDisplayMetrics)
        } else {
            wm.defaultDisplay.getMetrics(mDisplayMetrics)
        }
        return mDisplayMetrics.heightPixels
    }

    /**
     * 通过反射的方式获取状态栏高度
     * @param context 上下文对象
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("status_bar_height")
            val x = Integer.parseInt(field.get(obj).toString())
            return context.resources.getDimensionPixelSize(x)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

}