package com.juiceandroid.base_lib.tool.network.interceptor

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


class LoggingInterceptor constructor(private val logger: (message: String) -> Unit) : Interceptor {

    @Volatile
    var level = Level.NONE

    enum class Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        //判断显示级别
        val level = this.level
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val content = StringBuffer()
        val result: String

        //判断有没有请求体
        val requestBody = request.body
        val hasRequestBody = requestBody != null

        //获得链接
        val connection = chain.connection()
        //获得协议
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1

        content.append("接口:${request.url}\n")
            .append("请求相关:\n")
            .append("\t请求方式:${request.method}")
            .append("\t请求协议:${protocol(protocol)}\n")

        val startNs = System.nanoTime()
        //进行请求
        val response = chain.proceed(request)
        //花费的时间
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody?.contentLength() ?: 0L
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"

        if (logHeaders) {
            val headers = request.headers
            if (headers.size > 0) {
                content.append("请求头内容：\n")
                for (i in 0 until headers.size) {
                    val name = headers.name(i)
                    if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(
                            name,
                            ignoreCase = true
                        )
                    ) {
                        content.append("\t${headers.name(i)}\t${headers.value(i)}\n")
                    }
                }
            }

            if (!logBody || !hasRequestBody) {

            } else if (bodyEncoded(request.headers)) {

            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                val charset: Charset
                val contentType = requestBody.contentType()
                charset = contentType?.charset(UTF8) ?: UTF8

                val requestContent = buffer.readString(charset)
                if (requestContent.length < 3000) {
                    content.append("\t请求体内容：\n")
                        .append("\t\t$requestContent\n")
                } else {
                    content.append("\t请求体内容：\n")
                        .append("\t\t过长，省略\n")
                }
            }
        }

        content.append("响应相关:\n")
            .append("\t结果码${response.code}")
            .append("\t消息:${response.message}")
            .append("\t花费的时间:${tookMs}ms\n")
//                .append("\t响应体长度-->$bodySize\n")
        //响应部分
        if (logHeaders) {
            //响应头
            //val headers = response.headers()
            //响应体
            if (!logBody) {
            } else if (bodyEncoded(response.headers)) {
            } else {
                if (responseBody != null) {
                    val source = responseBody.source()
                    source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                    val buffer = source.buffer()
                    val charset: Charset
                    val contentType = responseBody.contentType()
                    charset = contentType?.charset(UTF8) ?: UTF8
                    if (contentLength != 0L) {
                        result = buffer.clone().readString(charset)
                        content.append("\t响应体内容(${buffer.size}byte):\n")
                            .append("\t\t$result\n")
                    }
                }
            }
        }

        logger(content.toString())

        return response
    }


    private fun bodyEncoded(headers: Headers): Boolean = !(headers.get("Content-Encoding")?.equals("identity", ignoreCase = true) ?: true)

    private fun protocol(protocol: Protocol): String = if (protocol === Protocol.HTTP_1_0) "HTTP/1.0" else "HTTP/1.1"

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }
}
