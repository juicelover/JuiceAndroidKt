package com.juiceandroid.base_lib.tool

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat

object PermissionTool {

    private val MIN_SDK_PERMISSIONS: SimpleArrayMap<String, Int> = SimpleArrayMap(8)

    init {
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14)
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20)
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16)
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16)
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9)
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16)
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23)
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23)
    }

    /**
     * 判断是否所有权限都同意了
     * @param context Context对象
     * @param permissions 权限数组
     * @return 都同意返回true 否则返回false
     */
    fun hasSelfPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (permissionExists(permission) && !hasSelfSinglePermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * 判断单个权限是否同意
     * @param context Context对象
     * @param permission 权限
     * @return 同意返回true 否则返回false
     */
    private fun hasSelfSinglePermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 判断权限是否存在
     * @param permission 权限
     * @return 存在返回true 否则返回false
     */
    private fun permissionExists(permission: String): Boolean {
        val minVersion = MIN_SDK_PERMISSIONS.get(permission)
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion
    }

    /**
     * 检查是否都赋予权限
     * @param grantResults 权限数组
     * @return 都赋予意返回true 否则返回false
     */
    fun verifyPermissions(vararg grantResults: Int): Boolean {
        if (grantResults.isEmpty()) return false
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 检查所给权限List是否需要给提示
     * @param activity Activity
     * @param permissions 权限数组
     * @return 需要给提示返回true 否则返回false
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 请求权限
     * @param context Context对象
     * @param needPermission 权限数组
     * @param grantedAction 请求成功后的操作
     */
    fun requestPermission(context: Context,needPermission:Array<String>,grantedAction:()->Unit ){
        requestPermissionWithErrorHandle(context,needPermission,0,grantedAction,null,null)
    }

    /**
     * 请求权限并对失败时候做处理
     * @param context Context对象
     * @param needPermission 权限数组
     * @param requestCode 请求码
     * @param grantedAction 请求成功后的操作
     * @param deniedAction 请求被拒绝后的操作
     * @param canceledAction 请求被取消后的操作
     */
    fun requestPermissionWithErrorHandle(context: Context,needPermission:Array<String>,requestCode:Int = 0,
                          grantedAction:()->Unit,
                          deniedAction:((requestCode: Int, denyList: List<String>)->Unit)?=null,
                          canceledAction:((requestCode: Int)->Unit)?=null,
    ){
        //所需授权为空，直接结束
        if(needPermission.isEmpty()){
            return
        }
        //所需授权已全部授予，直接执行请求成功后的操作
        if (hasSelfPermissions(context, *needPermission)){
            grantedAction.invoke()
            return
        }
        //打开透明的PermissionRequestActivity，在其上执行权限请求操作
        com.juiceandroid.base_lib.activity.PermissionRequestActivity.permissionRequest(context, needPermission,requestCode,object :
            com.juiceandroid.base_lib.activity.IPermission {
            override fun permissionGranted() {
                grantedAction.invoke()
            }

            override fun permissionDenied(requestCode: Int, denyList: List<String>) {
                deniedAction?.invoke(requestCode,denyList)
            }

            override fun permissionCanceled(requestCode: Int) {
                canceledAction?.invoke(requestCode)
            }
        })
    }
}
