package com.juiceandroid.base_lib.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.juiceandroid.base_lib.base.Presenter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * BaseFragment
 */
open abstract class BaseFragment<VB : ViewDataBinding> : Fragment(),
    Presenter {
    /**
     * 上下文对象
     */
    lateinit var mContext: Context

    /**
     * ViewDataBinding
     */
    protected lateinit var bindingView: VB

    /**
     * 是否显示
     */
    var visible = true

    /**
     * fragment在viewpager中是否已经显示
     */
    var initVisible = false

    private var lastVisibleMillis = 0L

    /**
     * fragment是否在展示
     */
    var fragmentShowStatus = false

    /**
     * 展示状态是否可改变
     */
    var fragmentShowStatusChangeAble = true

    /**
     * 锁定展示状态工作
     */
    var fragmentShowStatusLockStatusJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingView =
            DataBindingUtil.inflate(requireActivity().layoutInflater, getLayoutId(), null, false)
        return bindingView.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContext = requireActivity()
        retainInstance = true
        initView()
        loadData(true)
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
     * 加载数据
     * @param isRefresh 是否是刷新。一般为true。分页加载时使用（第一次加载：true  加载更多：false）。
     */
    abstract override fun loadData(isRefresh: Boolean)

    /**
     * 当fragment切换的时候调用
     * @param hidden 是否隐藏
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        fragmentShowFilter(!isHidden) {
            visible = it
            updateShowStatusInfo(it)
        }
    }

    /**
     * 当fragment结合viewpager使用的时候，这个方法会调用，进行显示变化监听
     * @param isVisibleToUser 是否对用户显示
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        fragmentShowFilter(userVisibleHint) {
            if (!initVisible) {
                visible = it
            } else {
                initVisible = true
            }
            updateShowStatusInfo(it)
        }
    }

    /**
     * onResume，进行显示变化监听
     */
    override fun onResume() {
        super.onResume()
        if (visible) {
            fragmentShowFilter(true) {
                updateShowStatusInfo(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (visible) {
            fragmentShowFilter(false) {
                updateShowStatusInfo(it)
            }
        }
    }

    /**
     * 点击事件
     * @param v 被点击的view
     */
    override fun onClick(v: View?) {

    }

    /**
     * fragmentShow监听过滤器，防止一次操作造成两次变化
     * @param show Fragment显示变化
     */
    private inline fun fragmentShowFilter(show: Boolean, action: (show: Boolean) -> Unit) {
        val nowTimeMillis = System.currentTimeMillis()
        if (nowTimeMillis - lastVisibleMillis > 300L) {
            lastVisibleMillis = nowTimeMillis
            action(show)
        }
    }

    /**
     * 更新展示状态信息
     */
    private fun updateShowStatusInfo(newStatus: Boolean) {
        if (fragmentShowStatus != newStatus) {
            fragmentShowStatus = newStatus
            onFragmentShow(newStatus)
            if (newStatus) {
                initVisible = true
            }
        } else {
            if (fragmentShowStatusChangeAble) {
                onFragmentShow(newStatus)
            }
        }
        fragmentShowStatusLockStatusJob?.cancel()
        fragmentShowStatusLockStatusJob = GlobalScope.launch {
            delay(100)
            fragmentShowStatusChangeAble = true
        }
    }

    /**
     * Fragment显隐变化监听
     * @param show Fragment是否显示
     */
    open fun onFragmentShow(show: Boolean) {

    }

    override fun baseApplySkin(activity: Activity) {

    }

    override fun setStatusBar(bar: ImmersionBar) {

    }
}