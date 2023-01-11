package com.dream.juiceandroid.developers.codelab

import android.content.Intent
import android.view.View
import com.dream.juiceandroid.R
import com.dream.juiceandroid.databinding.ActivityCodeLabsBinding
import com.dream.juiceandroid.developers.codelab.activity.DataBindingActivity
import com.juiceandroid.base_lib.activity.BaseActivity
import com.juiceandroid.base_lib.tool.AppTool

/**
 * @author juice
 * @date 2023-01-09 08:33:00
 * @desc Google Developers Codelabs
 * @link https://developers.google.cn/codelabs/?cat=Android
 */
class CodeLabsActivity : BaseActivity<ActivityCodeLabsBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_code_labs

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

                    R.id.tv_data_binding -> {
                        startActivity(Intent(this, DataBindingActivity::class.java))
                    }
                }
            }
        }
    }
}