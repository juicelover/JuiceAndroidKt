package com.juiceandroid.base_lib.tool

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juiceandroid.base_lib.R


object StatusBarTool {

    var navigationHeight = 0

    //是否测量存在刘海屏
    var hasExecuteNotch = false

    //是否存在刘海屏
    var hasNotch = false

    /**
     * 通过反射的方式获取状态栏高度
     * @param context 上下文对象，默认为ActivityMgr.getContext()
     * @return 状态栏高度
     */
    fun getStatusBarHeight(context: Context = ActivityMgr.getContext()): Int {
        try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("status_bar_height")
            val x = Integer.parseInt(field.get(obj).toString())
            return context.resources.getDimensionPixelSize(x)
        } catch (e: Exception) {
        }
        return try {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            context.resources.getDimensionPixelSize(resourceId)
        } catch (e: Exception) {
            1
        }
    }

    fun isNavigationBarShow(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display: Display = activity.windowManager.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            realSize.y !== size.y
        } else {
            val menu = ViewConfiguration.get(activity).hasPermanentMenuKey()
            val back =
                KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            !(menu || back)
        }
    }

    /**
     * 获取底部导航栏高度
     * @param context 上下文对象
     * @return 底部导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        return try {
            val resourceId =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            context.resources.getDimensionPixelSize(resourceId)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 设置状态栏
     * @param activity Activity
     * @param useThemestatusBarColor 是否要状态栏的颜色，不设置则为透明色
     * @param withoutUseStatusBarColor 是否不需要使用状态栏为暗色调
     */
    fun setStatusBar(
        activity: Activity,
        useThemestatusBarColor: Boolean,
        withoutUseStatusBarColor: Boolean
    ) {
        val decorView = activity.window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        if (useThemestatusBarColor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = Color.TRANSPARENT
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !withoutUseStatusBarColor) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    /**
     * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
     * @param isLightStatusBar 是否是亮状态栏
     * @param activity Activity
     */
    private fun processMEIZU(isLightStatusBar: Boolean, activity: Activity) {
        val lp = activity.window.attributes
        try {
            val instance = Class.forName("android.view.WindowManager\$LayoutParams")
            val value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp)
            val field = instance.getDeclaredField("meizuFlags")
            field.isAccessible = true
            val origin = field.getInt(lp)
            if (isLightStatusBar) {
                field.set(lp, origin or value)
            } else {
                field.set(lp, value.inv() and origin)
            }
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上  lightStatusBar为真时表示黑色字体
     * @param lightStatusBar 是否是亮状态栏
     * @param activity Activity
     */
    private fun processMIUI(lightStatusBar: Boolean, activity: Activity) {
        val clazz = activity.window.javaClass
        try {
            val darkModeFlag: Int
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            extraFlagField.invoke(
                activity.window,
                if (lightStatusBar) darkModeFlag else 0,
                darkModeFlag
            )
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

    }

    /**
     * 设置状态栏文字色值为深色调
     * @param useDart 是否使用深色调
     * @param activity Activity
     */
    fun setStatusTextColor(useDart: Boolean, activity: Activity) {
        when {
            DeviceTool.isMeizu -> processMEIZU(useDart, activity)
            DeviceTool.isMiui -> processMIUI(useDart, activity)
            else -> {
                if (useDart) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity.window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                } else {
                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
                activity.window.decorView.findViewById<View>(android.R.id.content)
                    .setPadding(0, 0, 0, navigationHeight)
            }
        }
    }

    /**
     * 显示StatusBar
     * @param view View
     */
    fun showStatusBar(view: View) =
        ViewCompat.getWindowInsetsController(view)?.show(WindowInsetsCompat.Type.statusBars())

    /**
     * 隐藏StatusBar
     * @param view View
     */
    fun hideStatusBar(view: View) =
        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.statusBars())

    /**
     * 显示导航栏
     * @param view View
     */
    fun showNavigationBar(view: View) {
        val hasMenuKey = ViewConfiguration.get(ActivityMgr.currentActivity()!!).hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (hasMenuKey || hasBackKey) {
            ViewCompat.getWindowInsetsController(view)
                ?.show(WindowInsetsCompat.Type.navigationBars())
        }
    }

    /**
     * 是否存在刘海屏
     * @return 是否存在刘海屏
     */
    fun hasNotch(activity: Activity? = ActivityMgr.currentActivity()): Boolean {
        isOtherBrandHasNotch(activity)
        if (!hasExecuteNotch) {
            hasNotch = hasNotchAtMIUI() || hasNotchAtHUAWEI() || hasNotchAtOPPO()
                    || hasNotchAtVIVO() || hasNotchAtSamsung() || isOtherBrandHasNotch(activity)
            hasExecuteNotch = true
        }
        return hasNotch
    }

    /**
     * 华为手机是否存在刘海屏
     * @return 是否存在刘海屏
     */
    private fun hasNotchAtHUAWEI(): Boolean {
        return try {
            val hwNotchSizeUtil =
                ActivityMgr.getContext().classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val method = hwNotchSizeUtil.getMethod("hasNotchInScreen")
            method.invoke(hwNotchSizeUtil) as Boolean
        } catch (e: java.lang.Exception) {
            false
        }
    }

    /**
     * 小米手机是否存在刘海屏
     * @return 是否存在刘海屏
     */
    private fun hasNotchAtMIUI(): Boolean {
        return if (DeviceTool.isMiui) {
            try {
                val systemProperties =
                    ActivityMgr.getContext().classLoader.loadClass("android.os.SystemProperties")
                val method = systemProperties.getMethod(
                    "getInt",
                    String::class.java,
                    Int::class.javaPrimitiveType
                )
                method.invoke(systemProperties, arrayOf("ro.miui.notch", 0)) == 1
            } catch (e: java.lang.Exception) {
                false
            }
        } else {
            false
        }
    }

    /**
     * OPPO手机是否存在刘海屏
     * @return 是否存在刘海屏
     */
    private fun hasNotchAtOPPO(): Boolean =
        ActivityMgr.getContext().packageManager
            .hasSystemFeature("com.oppo.feature.screen.heteromorphism")

    /**
     * VIVO手机是否存在刘海屏
     * @return 是否存在刘海屏
     */
    private fun hasNotchAtVIVO(): Boolean {
        return try {
            val ftFeature = ActivityMgr.getContext().classLoader.loadClass("android.util.FtFeature")
            val method =
                ftFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
            method.invoke(ftFeature, 0x00000020) as Boolean
        } catch (e: java.lang.Exception) {
            false
        }
    }

    /**
     * 三星手机是否存在刘海屏
     * @return 是否存在刘海屏
     */
    private fun hasNotchAtSamsung(): Boolean {
        if (DeviceTool.isSamsung) {
            try {
                ActivityMgr.getContext().resources.getIdentifier(
                    "config_mainBuiltInDisplayCutout",
                    "string",
                    "android"
                )
                val res = ActivityMgr.getContext().resources
                val resId =
                    res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android");
                val spec = if (resId > 0) {
                    res.getString(resId)
                } else {
                    null
                }
                return !spec.isNullOrEmpty()
            } catch (e: Exception) {
            }
        }
        return false;
    }

    /**
     * 其他手机是否存在刘海屏
     * @return 是否存在刘海屏
     */
    private fun isOtherBrandHasNotch(activity: Activity?): Boolean {
        if (activity != null && activity.window != null) {
            val decorView = activity.window.decorView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val windowInsets = decorView.rootWindowInsets
                if (windowInsets != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val displayCutout = windowInsets.displayCutout
                        if (displayCutout != null) {
                            val rects = displayCutout.boundingRects
                            if (rects.isNotEmpty()) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     * 获取刘海屏高度
     * @param activity Activity
     * @return 刘海屏高度
     */
    fun getNotchHeight(activity: Activity? = ActivityMgr.currentActivity()): Int {
        return if (hasNotch(activity)) {
            var height = 0
            if (DeviceTool.isHuawei) {
                height = getNotchSizeAtHuaWei()
            }
            if (height == 0){
                height = getOtherBrandNotchSize(activity)
            }
            if (height == 0){
                height = getStatusBarHeight()
            }
            height
        }else{
            0
        }
    }

    /**
     * 获取华为刘海屏高度
     * @return 刘海屏高度
     */
    private fun getNotchSizeAtHuaWei(): Int {
        return try {
            val hwNotchSizeUtil =
                ActivityMgr.getContext().classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val method = hwNotchSizeUtil.getMethod("getNotchSize")
            (method.invoke(hwNotchSizeUtil) as IntArray)[1]
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 获取其它品牌刘海屏高度
     * @return 刘海屏高度
     */
    private fun getOtherBrandNotchSize(activity: Activity?): Int {
        if (activity != null && activity.window != null) {
            val decorView = activity.window.decorView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val windowInsets = decorView.rootWindowInsets
                if (windowInsets != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val displayCutout = windowInsets.displayCutout
                        if (displayCutout != null) {
                            val rects: List<Rect?> = displayCutout.boundingRects
                            if (rects.isNotEmpty()) {
                                if (rects[0] != null) {
                                        return rects[0]?.bottom?:0
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0
    }

}