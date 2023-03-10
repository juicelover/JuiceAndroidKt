package com.dream.juiceandroid.developers

import android.content.Intent
import android.view.View
import com.dream.juiceandroid.R
import com.dream.juiceandroid.databinding.ActivityAndroidDevelopersBinding
import com.dream.juiceandroid.developers.codelab.CodeLabsActivity
import com.juiceandroid.base_lib.activity.BaseActivity
import com.juiceandroid.base_lib.tool.AppTool

/**
 * @author juice
 * @date 2023-01-09 08:31:15
 * @desc Android Developers
 * @link https://developer.android.google.cn/index.html
 */
class AndroidDevelopersActivity : BaseActivity<ActivityAndroidDevelopersBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_android_developers

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

                    R.id.tv_code_labs -> {
                        startActivity(Intent(this, CodeLabsActivity::class.java))
                    }
                }
            }
        }
    }
}