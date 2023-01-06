package com.juiceandroid.base_lib.tool

import android.os.Build
import java.io.IOException

object DeviceTool {
    /**
     * 获取厂商信息,判断是否需要手动开启获取设备号权限
     */
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    private const val KEY_EMUI_VERSION_NAME = "ro.build.version.emui"
    private const val KEY_DISPLAY = "ro.build.display.id"
    private const val BRAND_XIAOMI = "xiaomi"
    private const val BRAND_HUAWEI = "huawei"
    private const val BRAND_HONOUR = "honour"
    private const val BRAND_VIVO = "VIVO"
    private const val BRAND_IQOO = "IQOO"
    private const val BRAND_OPPO = "oppo"
    private const val BRAND_REALME = "realme"
    private const val BRAND_SAMSUNG = "samsung"
    private const val BRAND_MEIZU = "meizu"


    /**
     * 判断手机是否是小米
     */
    val isMiui: Boolean
        get() {
            return try {
                (Build.MANUFACTURER.equals(BRAND_XIAOMI, true)
                        || getSystemProperty(KEY_MIUI_VERSION_CODE).isNotEmpty()
                        || getSystemProperty(KEY_MIUI_VERSION_NAME).isNotEmpty()
                        || getSystemProperty(KEY_MIUI_INTERNAL_STORAGE).isNotEmpty())
            } catch (e: IOException) {
                false
            }
        }

    /**
     * 判断手机是否是魅族
     */
    val isMeizu: Boolean
        get() = Build.MANUFACTURER.equals(BRAND_MEIZU, true)

    /**
     * 判断手机是否是三星
     */
    val isSamsung: Boolean
        get() = Build.MANUFACTURER.equals(BRAND_SAMSUNG, true)

    /**
     * 判断手机是否是华为(包括其子品牌荣耀)
     */
    val isHuawei: Boolean
        get() = Build.MANUFACTURER.equals(BRAND_HUAWEI, true)||
                Build.MANUFACTURER.equals(BRAND_HONOUR, true)

    private fun getSystemProperty(key: String, defaultValue: String = ""): String {
        return try {
            val clz = Class.forName("android.os.SystemProperties")
            val get = clz.getMethod("get", String::class.java, String::class.java)
            get.invoke(clz, key, defaultValue) as String
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
            defaultValue
        }
    }
}