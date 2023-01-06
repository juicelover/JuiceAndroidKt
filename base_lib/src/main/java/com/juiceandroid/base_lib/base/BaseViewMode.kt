package com.juiceandroid.base_lib.base

import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juiceandroid.base_lib.tool.ActivityMgr
import com.juiceandroid.base_lib.tool.SingleLiveEvent
import com.juiceandroid.base_lib.tool.network.ExceptionHandle
import com.juiceandroid.base_lib.tool.print
import kotlinx.coroutines.*


open class BaseViewMode : ViewModel() {

    companion object {
        class UIChangeLiveData : SingleLiveEvent<Void>() {
            val showDialogEvent = SingleLiveEvent<String>()
            val dismissDialogEvent = SingleLiveEvent<Void>()
            val finishEvent = SingleLiveEvent<Void>()

            //val uiMessageEvent = SingleLiveEvent<UIMessage>()
            val toastEvent = SingleLiveEvent<String>()
        }
    }

    val uiChangeLiveData = UIChangeLiveData()

    /**
     * 请求结果过滤，判断请求服务器请求结果是否成功，不成功则会抛出异常
     */
    private suspend fun <T> executeResponse(
        response: BaseResponse<T>,
        success: suspend CoroutineScope.(T?) -> Unit
    ) {
        coroutineScope {
            when (response.code) {
                0, 200 -> {
                    success(response.data)
                }
                401 -> {
                    //token 失效重新登录
                    val intent = Intent("com.icesfox.notify.MY_BROADCAST")
                    intent.component =
                        ComponentName(
                            "com.icesfox.notify",
                            "com.icesfox.notify.tools.LoginAgainReceiver"
                        )
                    ActivityMgr.getContext().sendBroadcast(intent)
                }
                else -> {
                    throw AppException(response.code, response.msg)
                }
            }
        }
    }


    /**
     * 过滤服务器结果，失败直接抛异常，没有回到异常信息
     * @param block 请求体方法，必须要用suspend关键字修饰
     * @param success 成功回调
     * @param isShowDialog 是否显示加载框
     */
    fun <T> request(
        block: suspend () -> BaseResponse<T>,
        success: (T?) -> Unit,
        error: (Throwable) -> Unit,
        isShowDialog: Boolean = true,
    ): Job {
        //如果需要弹窗 通知Activity/fragment弹窗
        return viewModelScope.launch {
            runCatching {
                if (isShowDialog) uiChangeLiveData.showDialogEvent.postValue("加载中")
                //请求体
                block()
            }.onSuccess {
                //网络请求成功 关闭弹窗
                uiChangeLiveData.dismissDialogEvent.postValue(null)
                runCatching {
                    //校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
                    executeResponse(it) { t ->
                        success(t)
                    }
                }.onFailure {
                    //失败回调
                    ExceptionHandle.handleException(it).message?.apply {
                        //打印错误消息
                        "失败--->$this".print()
                        if (this == "token失效，请重新登录") {
                            val intent = Intent("com.icesfox.notify.MY_BROADCAST")
                            intent.component =
                                ComponentName(
                                    "com.icesfox.notify",
                                    "com.icesfox.notify.tools.LoginAgainReceiver"
                                )
                            ActivityMgr.getContext().sendBroadcast(intent)
                        }
                        uiChangeLiveData.toastEvent.postValue(this)
                    }
                    error.invoke(it)
                }
            }.onFailure {
                //网络请求异常 关闭弹窗
                uiChangeLiveData.dismissDialogEvent.postValue(null)
                //失败回调
                ExceptionHandle.handleException(it).message?.apply {
                    //打印错误消息

                    "失败--->$this".print()
                    uiChangeLiveData.toastEvent.postValue(this)
                }

                error.invoke(it)
            }
        }
    }

    /**
     * 过滤服务器结果，失败抛异常
     * @param block 请求体方法，必须要用suspend关键字修饰
     * @param success 成功回调
     * @param error 失败回调 可不传
     * @param isShowDialog 是否显示加载框
     * @param loadingMessage 加载框提示内容
     */
    fun <T> request(
        block: suspend () -> BaseResponse<T>,
        success: (T?) -> Unit,
        error: (AppException) -> Unit = {},
        isShowDialog: Boolean = true,
        loadingMessage: String = "请求网络中..."
    ): Job {
        //如果需要弹窗 通知Activity/fragment弹窗
        return viewModelScope.launch {
            runCatching {
                if (isShowDialog) uiChangeLiveData.showDialogEvent.postValue(loadingMessage)
                //请求体
                block()
            }.onSuccess {
                //网络请求成功 关闭弹窗
                uiChangeLiveData.dismissDialogEvent.postValue(null)
                runCatching {
                    //校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
                    executeResponse(it) { t ->
                        success(t)
                    }
                }.onFailure {
                    error(ExceptionHandle.handleException(it))
                    //打印错误消息
                    ExceptionHandle.handleException(it).message?.apply {
                        //打印错误消息
                        "失败--->$this".print()
                        if (this == "token失效，请重新登录") {
                            val intent = Intent("com.icesfox.notify.MY_BROADCAST")
                            intent.component =
                                ComponentName(
                                    "com.icesfox.notify",
                                    "com.icesfox.notify.tools.LoginAgainReceiver"
                                )
                            ActivityMgr.getContext().sendBroadcast(intent)
                        }

                        uiChangeLiveData.toastEvent.postValue(this)
                    }
                }
            }.onFailure {
                //网络请求异常 关闭弹窗
                uiChangeLiveData.dismissDialogEvent.postValue(null)
                error(ExceptionHandle.handleException(it))
                //失败回调
                ExceptionHandle.handleException(it).message?.apply {
                    //打印错误消息
                    //L.e(this)
                    "失败--->$this".print()
                    uiChangeLiveData.toastEvent.postValue(this)
                }
            }
        }
    }


    /**
     *  不过滤请求结果
     * @param block 请求体 必须要用suspend关键字修饰
     * @param success 成功回调
     * @param error 失败回调 可不给
     * @param isShowDialog 是否显示加载框
     * @param loadingMessage 加载框提示内容
     */
    fun <T> requestNoCheck(
        block: suspend () -> T,
        success: (T) -> Unit,
        error: (AppException) -> Unit = {},
        isShowDialog: Boolean = true,
        loadingMessage: String = "请求网络中..."
    ): Job {
        //如果需要弹窗 通知Activity/fragment弹窗
        if (isShowDialog) uiChangeLiveData.showDialogEvent.postValue(loadingMessage)
        return viewModelScope.launch {
            runCatching {
                //请求体
                block()
            }.onSuccess {
                //网络请求成功 关闭弹窗
                uiChangeLiveData.dismissDialogEvent.postValue(null)
                //成功回调
                success(it)
            }.onFailure {
                //网络请求异常 关闭弹窗
                uiChangeLiveData.dismissDialogEvent.postValue(null)
                error(ExceptionHandle.handleException(it))
                //失败回调
                ExceptionHandle.handleException(it).message?.apply {
                    if (this == "token失效，请重新登录") {
                        val intent = Intent("com.icesfox.notify.MY_BROADCAST")
                        intent.component =
                            ComponentName(
                                "com.icesfox.notify",
                                "com.icesfox.notify.tools.LoginAgainReceiver"
                            )
                        ActivityMgr.getContext().sendBroadcast(intent)
                    }
                    //打印错误消息
                    uiChangeLiveData.toastEvent.postValue(this)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}