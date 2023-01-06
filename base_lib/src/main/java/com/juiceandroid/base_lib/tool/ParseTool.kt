package com.juiceandroid.base_lib.tool

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * px转换sp
 */
fun Float.px2sp(context: Context = ActivityMgr.getContext()): Int =
    (this / context.applicationContext.resources.displayMetrics.scaledDensity + 0.5f).toInt()

fun Int.px2sp(context: Context = ActivityMgr.getContext()): Int =
    (this / context.applicationContext.resources.displayMetrics.scaledDensity + 0.5f).toInt()

/**
 * sp转换px
 */
fun Float.sp2px(context: Context = ActivityMgr.getContext()): Int =
    (this * context.applicationContext.resources.displayMetrics.scaledDensity + 0.5f).toInt()

fun Int.sp2px(context: Context = ActivityMgr.getContext()): Int =
    (this * context.applicationContext.resources.displayMetrics.scaledDensity + 0.5f).toInt()

/**
 * px转换dp
 */
fun Float.px2dp(context: Context = ActivityMgr.getContext()): Int =
    (this / context.applicationContext.resources.displayMetrics.density + 0.5f).toInt()

fun Int.px2dp(context: Context = ActivityMgr.getContext()): Int =
    (this / context.applicationContext.resources.displayMetrics.density + 0.5f).toInt()

/**
 * dp转换px
 */
fun Float.dp2px(context: Context = ActivityMgr.getContext()): Int =
    (this * context.applicationContext.resources.displayMetrics.density + 0.5f).toInt()

fun Int.dp2px(context: Context = ActivityMgr.getContext()): Int =
    (this * context.applicationContext.resources.displayMetrics.density + 0.5f).toInt()


/**
 * 毫秒时间戳 转换成日期
 * @param regex 格式化时间格式
 * */
@SuppressLint("SimpleDateFormat")
fun String.strToTime(regex: String = "yyyy-MM-dd HH:mm:ss"): String? {
    val simpleDateFormat = SimpleDateFormat(regex)
    //如果它本来就是long类型的,则不用写这一步
    val date = Date(this.toLong().orZero())
    return simpleDateFormat.format(date)
}

/**
 * 将请求参数Map转换为请求体
 * @receiver 存放请求参数的Map
 * @param existPublicParm 是否有公共参数，默认存在公共参数
 * @return RequestBody
 */
fun HashMap<String, Any?>.toRequestBody(existPublicParm: Boolean = true): RequestBody {
    if (existPublicParm) {
        //if (!this.containsKey("app_name")) {
        //    this["app_name"] = AppConfig.APP_NAME
        //}
    }
    return RequestBody.create(
        "application/json; charset=utf-8".toMediaTypeOrNull(), this.toJson() ?: ""
    )
}
/**
 *
 * double 转 double ，四舍五入保留两位小数。
 */
private fun toDoubleTwoFormat(value: Double): Double {
    return ((value + 0.0000001) * 100).roundToInt().toDouble() / 100
}

/**
 * double 转 string ，四舍五入保留两位小数。
 *  */
fun toStringTwoFormat(value: Double): String {
    return String.format("%.2f", toDoubleTwoFormat(value))
}

/**
 * double 转 string ，四舍五入保留两位小数。
 *  */
fun toStringOneFormat(value: Double): String {
    return String.format("%.1f", toDoubleTwoFormat(value))
}

/**
 * Long 为null 的扩展函数
 */
fun Long?.orZero(): Long {
    return this ?: 0L
}

/**
 * int 为null 的扩展函数
 */
fun Int?.orZero(): Int {
    return this ?: 0
}

/**
 * 判断是否为空 或者 为null的情况
 * null："null"
 * null: "(null)"
 * null: "<null>"
 */
fun String?.isNotNullEmpty(): Boolean {
    return this != null && this.isNotEmpty() && this.toLowerCase(Locale.ENGLISH) != "null" && this.toLowerCase(
        Locale.ENGLISH
    ) != "(null)" && this.toLowerCase(Locale.ENGLISH) != "<null>"
}

/**
 * 获取assets 内容
 */
fun getAssetsJson(fileName: String?, context: Context): String {
    //将json数据变成字符串
    val stringBuilder = StringBuilder()
    try {
        //获取assets资源管理器
        val assetManager = context.assets
        //通过管理器打开文件并读取
        val bf = BufferedReader(
            InputStreamReader(
                assetManager.open(fileName!!)
            )
        )
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}