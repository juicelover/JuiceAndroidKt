package com.juiceandroid.base_lib.base


interface RepositoryResult<T> {

    /**
     * 获取数据成功
     * @param result 数据结果
     */
    fun onSuccessful(result: T?)

    /**
     * 获取数据失败
     * @param errorHint 错误结果
     * @param errorCode 错误码
     */
    fun onFail(errorHint: String, errorCode: Int)
}