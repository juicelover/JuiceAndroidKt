package com.juiceandroid.base_lib.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.gyf.immersionbar.ImmersionBar
import com.juiceandroid.base_lib.base.Presenter
import com.juiceandroid.base_lib.tool.DarkModeUtils

/**
 *  base
 */
abstract class BaseActivity<VB : ViewDataBinding> : FragmentActivity(),
    Presenter {

    lateinit var mContext: Context

    /**
     * 是否自动调用loadData()方法加载数据，默认为true
     */
    open val autoRefresh = true

    // 布局view
    lateinit var bindingView: VB

    /**
     * onCreate中方法执行顺序：设置页面布局->初始化页面（子类待实现）->如果autoRefresh为true，加载数据（子类待实现）
     * @param savedInstanceState Bundle
     */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //默认竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mContext = this
        //ARouter.getInstance().inject(this)
        setContentView(getLayoutId())
        setStatusBar(ImmersionBar.with(this))
        initView()
        if (autoRefresh) {
            loadData(true)
        }
    }

    /**
     * 根据是否显示ToolBar加载布局文件
     * @param layoutResID 布局资源文件
     */
    @SuppressLint("NewApi", "SourceLockedOrientationActivity")
    override fun setContentView(@LayoutRes layoutResID: Int) {
        bindingView = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            layoutResID, null, false
        )
        super.setContentView(bindingView.root)
    }

    /**
     * 对返回键做特殊处理
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val curr = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        DarkModeUtils.applySystemMode(this)
        baseApplySkin(this)
    }


    override fun baseApplySkin(activity: Activity) {
    }

    override fun setStatusBar(bar: ImmersionBar) {

    }

    /**
     * 点击事件
     * @param v 被点击的view
     */
    override fun onClick(v: View?) {

    }

    /**
     * 获取布局文件
     * @return 布局文件
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化页面
     */
    abstract fun initView()

    /**
     * 返回。toolbar，返回键均会调用。
     */
    open fun back() {
        finish()
    }
}