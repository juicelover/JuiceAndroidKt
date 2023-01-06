package com.juiceandroid.base_lib.tool

import android.os.Handler
import android.os.Message

class HandlerProxy(private val mHandler: Handler) : Handler() {

    /**
     * 代理发送消息
     * @param msg 发送的消息
     */
    override fun handleMessage(msg: Message) {
        try {
            mHandler.handleMessage(msg)
        } catch (e: Throwable) {
        }

    }
}