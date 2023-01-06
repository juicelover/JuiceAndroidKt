package com.juiceandroid.base_lib.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.juiceandroid.base_lib.R
import com.juiceandroid.base_lib.tool.PermissionTool

class PermissionRequestActivity : Activity() {

    private var permissions: Array<String>? = null
    private var requestCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_permission)
        ImmersionBar.with(this)
                .navigationBarEnable(false)
                .statusBarDarkFont(true)
                .hideBar(BarHide.FLAG_SHOW_BAR).init()
        val bundle = intent.extras
        if (bundle != null) {
            permissions = bundle.getStringArray(PERMISSION_KEY)
            requestCode = bundle.getInt(REQUEST_CODE, 0)
        }
        if (permissions == null || permissions!!.isEmpty()) {
            finish()
            return
        }
        requestPermission(permissions!!)
    }


    /**
     * 申请权限
     * @param permissions 所请求权限数组
     */
    private fun requestPermission(permissions: Array<String>) {
        if (PermissionTool.hasSelfPermissions(this, *permissions)) {
            if (permissionListener != null) {
                permissionListener!!.permissionGranted()
                permissionListener = null
            }
            finish()
            overridePendingTransition(0, 0)
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,grantResults: IntArray) {

        if (PermissionTool.verifyPermissions(*grantResults)) {
            //所有权限都同意
            if (permissionListener != null) {
                permissionListener?.permissionGranted()
            }
        } else {
            if (!PermissionTool.shouldShowRequestPermissionRationale(this, *permissions)) {
                //权限被拒绝并且选中不再提示
                if (permissions.size != grantResults.size) return
                val denyList =  mutableListOf<String>()
                for (i in grantResults.indices) {
                    if (grantResults[i] == -1) {
                        denyList.add(permissions[i])
                    }
                }
                if (permissionListener != null) {
                    permissionListener!!.permissionDenied(requestCode, denyList)
                }
            } else {
                //权限被取消
                if (permissionListener != null) {
                    permissionListener!!.permissionCanceled(requestCode)
                }
            }

        }
        permissionListener = null
        finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        private var permissionListener: IPermission? = null
        private const val PERMISSION_KEY = "permission_key"
        private const val REQUEST_CODE = "request_code"

        /**
         * 跳转到Activity申请权限
         * @param context Context对象
         * @param permissions 所请求权限数组
         * @param requestCode 请求码
         * @param iPermission 请求接口
         */
        fun permissionRequest(context: Context, permissions: Array<String>, requestCode: Int, iPermission: IPermission) {
            permissionListener = iPermission
            val intent = Intent(context, PermissionRequestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val bundle = Bundle()
            bundle.putStringArray(PERMISSION_KEY, permissions)
            bundle.putInt(REQUEST_CODE, requestCode)
            intent.putExtras(bundle)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(0, 0)
            }

        }
    }
}

/**
 * 权限获取结果接口
 */
interface IPermission {

    /**
     * 同意权限
     */
    fun permissionGranted()

    /**
     * 拒绝权限并且选中不再提示
     * @param requestCode 请求码
     * @param denyList 被拒绝的权限
     */
    fun permissionDenied(requestCode: Int, denyList: List<String>)

    /**
     * 取消授权
     * @param requestCode 请求码
     */
    fun permissionCanceled(requestCode: Int)
}