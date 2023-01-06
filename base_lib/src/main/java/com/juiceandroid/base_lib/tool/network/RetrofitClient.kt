package com.juiceandroid.base_lib.tool.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.juiceandroid.base_lib.tool.network.interceptor.LoggingInterceptor
import com.juiceandroid.base_lib.tool.print
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * @ClassName: RetrofitClient
 * @Description: 基础网络请求
 * @Author: 1244300796@qq.com
 * @Date: 2021/9/13 15:12
 */
object RetrofitClient {

    //接口地址前缀
    private const val BaseUri = "http://150.138.90.9:8001/notify-admin/"
//    private const val BaseUri = "http://notify.icesfox.com:9001/notify-admin/"

    private val mGson: Gson = GsonBuilder()
        .setLenient() // 设置GSON的非严格模式setLenient()
        .create()
    /**
     * 公开公共获取网络请求方法
     */
    fun getDefaultRetrofit(): Retrofit =
        Retrofit.Builder()
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .baseUrl(BaseUri)
            .build()

    fun getRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
    }


    fun getOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(20L, TimeUnit.SECONDS)//连接超时时间
            .writeTimeout(60L, TimeUnit.SECONDS)//写入超时时间
            .connectionPool(ConnectionPool(8, 15, TimeUnit.SECONDS))//连接池
            .addInterceptor(LoggingInterceptor(logger = { message -> message.print("-NET-") }).apply {
                level = LoggingInterceptor.Level.BODY
            })//日志拦截器
            .build()
}