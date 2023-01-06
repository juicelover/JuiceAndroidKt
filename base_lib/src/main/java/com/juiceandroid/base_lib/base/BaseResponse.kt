package com.juiceandroid.base_lib.base

/**
 * @Date :2022/7/20
 * @Description:
 * @Author: 1244300796@qq.com
 */
open class BaseResponse<T> {

    var msg: String? = null

    var code = 0

    var data: T? = null
}