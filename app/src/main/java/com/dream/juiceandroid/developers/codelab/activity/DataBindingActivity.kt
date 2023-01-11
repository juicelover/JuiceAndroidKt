package com.dream.juiceandroid.developers.codelab.activity

import com.dream.juiceandroid.R
import com.dream.juiceandroid.databinding.ActivityDataBindingBinding
import com.dream.juiceandroid.developers.codelab.viewModel.DataBindingViewModel
import com.juiceandroid.base_lib.activity.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author juice
 * @date 2023-01-09 08:57:20
 * @desc dataBinding
 * @link https://developers.google.cn/codelabs/android-databinding#0
 */
class DataBindingActivity : BaseActivity<ActivityDataBindingBinding>() {

    private val mViewModel by viewModel<DataBindingViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_data_binding

    override fun initView() {

        bindingView.lifecycleOwner = this
        bindingView.vm = mViewModel

        bindingView.toolbarJuiceCreate.apply {
            setNavigationOnClickListener { finish() }
        }
    }

    override fun loadData(isRefresh: Boolean) {

    }
}