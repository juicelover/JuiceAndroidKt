package com.juiceandroid.base_lib.tool

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

/**
 * 扩展方法，解析指定json字符串
 * @receiver json字符串
 * @param cls 解析结果类型类
 * @return T? 解析结果,无法解析返回null
 */
fun <T>  String.parseJson(cls: Class<T>): T? {
    try {
        return Gson().fromJson(this, cls)
    } catch (e: JsonSyntaxException) {
        e.message?.print("JSON", Log.ERROR)
    } catch (e: Exception) {
        e.message?.print("JSON", Log.ERROR)
    }
    return null
}

/**
 * 扩展方法，解析指定json字符串
 * @receiver json字符串
 * @param type 解析结果类型类
 * @return T? 解析结果,无法解析返回null
 */
fun <T>  String.parseJson(type: Type): T? {
    try {
        return Gson().fromJson<T>(this, type)
    } catch (e: JsonSyntaxException) {
        e.message?.print("JSON", Log.ERROR)
    } catch (e: Exception) {
        e.message?.print("JSON", Log.ERROR)
    }
    return null
}

/**
 * 扩展方法，将指定对象转换成json字符串
 * @receiver Any 待转换对象
 * @return String? json字符串,无法转换返回null
 */
fun Any.toJson(): String? {
    try {
        return Gson().toJson(this)
    } catch (e: JsonSyntaxException) {
        e.message?.print("JSON", Log.ERROR)
    } catch (e: Exception) {
        e.message?.print("JSON", Log.ERROR)
    }
    return null
}