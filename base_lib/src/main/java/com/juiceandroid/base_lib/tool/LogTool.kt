package com.juiceandroid.base_lib.tool

import android.util.Log
import com.orhanobut.logger.Logger

/**
 * 显示log
 * @receiver String log内容
 * @param tag String? 标签内容，默认为null，传null的话使用默认标签
 * @param type log类型
 */
fun String.print(tag: String? = null, type: Int = Log.INFO) {
    when (type) {
        Log.INFO -> if (tag.isNullOrEmpty()) Logger.i(this) else Logger.t(tag).i(this)
        Log.VERBOSE -> if (tag.isNullOrEmpty()) Logger.v(this) else Logger.t(tag).v(this)
        Log.DEBUG -> if (tag.isNullOrEmpty()) Logger.d(this) else Logger.t(tag).d(this)
        Log.WARN -> if (tag.isNullOrEmpty()) Logger.w(this) else Logger.t(tag).w(this)
        else -> if (tag.isNullOrEmpty()) Logger.e(this) else Logger.t(tag).e(this)
    }
}

fun String.printError(tag: String? = null){
    if (tag.isNullOrEmpty()) Logger.e(this) else Logger.t(tag).e(this)
}