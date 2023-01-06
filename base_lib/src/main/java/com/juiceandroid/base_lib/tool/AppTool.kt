package com.juiceandroid.base_lib.tool

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.juiceandroid.base_lib.R
import java.util.*


object AppTool {

    var width = 0

    var height = 0

    /**
     * 对宽高进行赋值
     *
     * @param context
     */
    fun initWidthAndHeight(context: Context) {
        width = getScreenWidth(context)
        height = getScreenHeight(context) + getStatusBarHeight(context)
    }

    /**
     * 显示区域高度
     * 即屏幕高度-底部状态栏高度。
     * 为了防止底部状态栏状态获取不准确导致计算显示区域高度出错。
     * 在Activity的根View上设置addOnLayoutChangeListener来取值。
     */
    var showContentHeight: Int = getScreenHeight()

    /**
     * 关闭软键盘
     */
    fun closeKeyboard(activity: Activity) {
        val view = activity.window.peekDecorView()
        if (view != null) {
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }

    /**
     * 打开软键盘
     */
    fun showKeyboard(activity: Activity, view: View) {
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            view,
            0
        )
    }

    /**
     * 打开软键盘
     */
    fun showKeyboard(activity: Activity) {
        activity.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        )
        //InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(editText, 0);
    }

    /**
     * 获取app的存储目录
     * 一般情况下是这样的/data/user/0/包名/cache
     * @param context 上下文对象，默认为ActivityMgr.getContext()
     * @return 存储目录
     */
    fun getAppDir(context: Context? = ActivityMgr.getContext()): String = context!!.cacheDir.path

    /**
     * 获取屏幕宽度
     * @param context 上下文对象，不能为EggApplication
     * @return 屏幕宽度，单位PX
     */
    fun getScreenWidth(
        context: Context? = ActivityMgr.currentActivity() ?: ActivityMgr.getContext()
    ): Int {
        return try {
            val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val mDisplayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.getRealMetrics(mDisplayMetrics)
            } else {
                wm.defaultDisplay.getMetrics(mDisplayMetrics)
            }
            mDisplayMetrics.widthPixels
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 获取屏幕高度
     * @param context 上下文对象，不能为EggApplication
     * @return 屏幕高度，单位PX
     */
    fun getScreenHeight(
        context: Context? = ActivityMgr.currentActivity()
            ?: ActivityMgr.getContext()
    ): Int {
        return try {
            val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val mDisplayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context?.display?.getRealMetrics(mDisplayMetrics)
            } else {
                wm.defaultDisplay.getMetrics(mDisplayMetrics)
            }
            mDisplayMetrics.heightPixels
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 获取当前版本号码和名称
     * @param context 上下文对象，默认为ActivityMgr.getContext()
     * @return PackageInfo对象
     */
    fun getCurrentVersion(context: Context? = ActivityMgr.getContext()): PackageInfo? {
        return try {
            context?.packageManager?.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 防止连续点击
     * @param view View
     * @param action Function0<Unit>
     */
    fun singleClick(view: View, time: Int = MIN_CLICK_DELAY_TIME, action: () -> Unit) {
        val tag = view.getTag(TIME_TAG)
        val lastClickTime = if (tag != null && tag is Long) tag else 0
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > time) {
            view.setTag(TIME_TAG, currentTime)
            action.invoke()
        }
    }

    private const val MIN_CLICK_DELAY_TIME = 500
    private var TIME_TAG = R.id.click_time

    //震动milliseconds毫秒
    fun vibrate(activity: Context, milliseconds: Long) {
        val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(milliseconds)
    }

    //以pattern[]方式震动
    fun vibrate(
        activity: Context,
        pattern: LongArray = longArrayOf(0, 1000, 0, 1000),
        repeat: Int = 0
    ) {
        val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(pattern, repeat)
    }

    //取消震动
    fun virateCancle(activity: Context) {
        val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.cancel()
    }

    /**
     * 通过反射的方式获取状态栏高度
     * @param context 上下文对象
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }
}

