package com.dream.juiceandroid

import android.content.Intent
import android.view.View
import com.dream.juiceandroid.databinding.ActivityMainBinding
import com.dream.juiceandroid.developers.AndroidDevelopersActivity
import com.juiceandroid.base_lib.activity.BaseActivity
import com.juiceandroid.base_lib.tool.AppTool

/**
 * @author juice
 * @date 2023-01-09 08:31:30
 * @desc Home Page
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {

        bindingView.lifecycleOwner = this
        bindingView.presenter = this

        bindingView.toolbarJuiceCreate.apply {
            setNavigationOnClickListener { finish() }
        }
    }

    override fun loadData(isRefresh: Boolean) {

    }

    override fun onClick(v: View?) {
        v?.let {
            AppTool.singleClick(v) {
                when (v.id) {

                    R.id.tv_android_developers -> {
                        startActivity(Intent(this, AndroidDevelopersActivity::class.java))
                    }
                }
            }
        }
    }
}