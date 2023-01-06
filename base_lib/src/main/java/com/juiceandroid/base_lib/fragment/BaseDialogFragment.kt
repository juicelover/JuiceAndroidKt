package com.juiceandroid.base_lib.fragment

import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.juiceandroid.base_lib.base.Presenter
import com.juiceandroid.base_lib.tool.ActivityMgr
import com.juiceandroid.base_lib.tool.AppTool.getScreenWidth


abstract class BaseDialogFragment<VB : ViewDataBinding>: DialogFragment(),
    Presenter {

    /**
     * ViewDataBinding
     */
    protected lateinit var bindingView: VB

    /**
     * 对话框宽
     */
    open var dialogWidth =  try{
        getScreenWidth(context?: ActivityMgr.currentActivity()!!)
    }catch (e:Exception){
        0
    }

    /**
     * 对话框长
     */
    open var dialogHeight =  ViewGroup.LayoutParams.WRAP_CONTENT

    /**
     * 浮层透明度
     */
    open val dimAmount = 0.7f

    /**
     * 对话框位置
     */
    open val gravity = Gravity.CENTER

    /**
     * 动画
     */
    open val animStyle:Int? = null

    /**
     * 是否可取消
     */
    open var cancelAble = true

    /**
     * 点击外部是否可取消
     */
    open var outsideCancelAble = true

    var res: Resources = try {
        resources
    }catch (e:Exception){
        (context?: ActivityMgr.currentActivity()?: ActivityMgr.getContext()).resources
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
     * 点击事件
     * @param v 被点击的view
     */
    override fun onClick(v: View?) {}

    /**
     * 加载数据
     * @param isRefresh 是否第一次加载数据
     */
    override fun loadData(isRefresh: Boolean) {}

    /**
     * 退出
     */
    open fun exit(){
        dismiss()
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }
}