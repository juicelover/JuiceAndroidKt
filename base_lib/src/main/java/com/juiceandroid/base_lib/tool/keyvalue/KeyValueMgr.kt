package com.juiceandroid.base_lib.tool.keyvalue

import android.os.Parcelable


interface KeyValueMgr{
    /**
     * 存值
     * @param key 键
     * @param value 值
     * @return 是否存放成功
     */
    fun put(key:String,value:Any):Boolean

    /**
     * 取值
     * @param key 键
     * @param defaultValue 默认值,可不传
     * @return 取值结果
     */
    fun <T>get(key:String,defaultValue:T?=null):T?

    /**
     * 取对象值
     * @param key 键
     * @param tClass 对象类型
     * @param defaultValue 默认值,可不传
     * @return 取值结果
     */
    fun <T:Parcelable>getObject(key:String,tClass: Class<T>,defaultValue:T?=null):T?

    /**
     * 移出指定值
     * @param key 键
     */
    fun remove(key:String)

    /**
     * 查看是否存在指定值
     * @param key 键
     * @return 是否存在
     */
    fun contains(key:String):Boolean

    /**
     * 清除所有值
     */
    fun clear()
}