package com.dream.juiceandroid.developers.codelab.activity

import android.content.Intent
import android.view.View
import com.dream.juiceandroid.R
import com.dream.juiceandroid.databinding.ActivityCodeLabsBinding
import com.juiceandroid.base_lib.activity.BaseActivity
import com.juiceandroid.base_lib.tool.AppTool

class DataBindingActivity : BaseActivity<ActivityCodeLabsBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_code_labs

    override fun initView() {

        bindingView.lifecycleOwner = this
        bindingView.presenter = this

        bindingView.toolbarNoticeCreate.apply {
            setNavigationOnClickListener { finish() }
        }
    }

    override fun loadData(isRefresh: Boolean) {

    }

    override fun onClick(v: View?) {
        v?.let {
            AppTool.singleClick(v) {
                when (v.id) {

                    R.id.tv_data_binding -> {
//                        startActivity(Intent(this, CodeLabsActivity::class.java))
                    }
                }
            }
        }
    }
}