package com.dream.juiceandroid

import android.content.Context
import android.os.Environment
import androidx.multidex.MultiDexApplication
import com.getkeepsafe.relinker.ReLinker
import com.juiceandroid.base_lib.tool.ActivityMgr
import com.juiceandroid.base_lib.tool.CrashHandler
import com.juiceandroid.base_lib.tool.DarkModeUtils
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

/**
 * @Date :2022/7/5
 * @Description: 主程序入口
 * @Author: 1244300796@qq.com
 */
class JuiceAndroidApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
//        initBugLy()
        initActivityManager()
        initKoin()
        initWrapMMKV(this)
        initLog()
        crashHandlerInit()
        DarkModeUtils.init(this)
    }

//    private fun initBugLy() {
//        Bugly.init(applicationContext, "fb613aa795", true)
//    }

    /**
     * 初始化MMKV
     * @param context Context
     */
    private fun initWrapMMKV(context: Context) {
        try {
            initMMKV(context)
        } catch (e: UnsatisfiedLinkError) {
            initMMKV(context) { s: String? -> ReLinker.loadLibrary(context, s) }
        }
    }

    private fun initMMKV(context: Context, loader: MMKV.LibLoader? = null) {
        try {
            val root = context.filesDir.absolutePath + "/mmkv"
            MMKV.initialize(root, loader)
        } catch (e: NullPointerException) {
            var cacheDir = context.cacheDir
            if (cacheDir == null) {
                cacheDir = Environment.getRootDirectory()
            }
            MMKV.initialize("$cacheDir/mmkv", loader)
        }
    }

    /**
     * 初始化日志
     */
    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Logger.init(this.packageName).methodOffset(2).methodCount(1).hideThreadInfo()
        } else {
            Logger.init().logLevel(LogLevel.NONE)
        }
        //Logger.init(this.packageName).methodOffset(2).methodCount(1).hideThreadInfo()
    }

    /**
     * 初始化Activity管理器
     */
    private fun initActivityManager() {
        ActivityMgr.init(this)
    }

    /**
     * 初始化Koin
     */
    private fun initKoin() {
        startKoin(applicationContext, appModule, logger = AndroidLogger())
//        startKoin{
//            androidLogger(Level.INFO)
//            androidContext(this@JuiceAndroidApplication)
//            modules(appModule)
//        }
    }

    /**
     * 本地崩溃日志采集
     */
    private fun crashHandlerInit() {
        if (!BuildConfig.DEBUG) {
            CrashHandler.getInstance().init(this)
        }
    }
}