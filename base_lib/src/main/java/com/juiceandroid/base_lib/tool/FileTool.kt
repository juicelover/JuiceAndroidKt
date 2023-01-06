package com.juiceandroid.base_lib.tool

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader


object FileTool {
    /**
     * 获取json文件内容工具类
     * @param context Context
     * @param fileName String
     * @return String
     */
    fun getAssetsFileText(context: Context, fileName:String):String{
        val strBuilder=StringBuilder()
        val assetManager=context.assets
        val bf = BufferedReader(InputStreamReader(assetManager.open(fileName)))
        bf.use { strBuilder.append(it.readLine())}
        bf.close()
        return strBuilder.toString()
    }

}