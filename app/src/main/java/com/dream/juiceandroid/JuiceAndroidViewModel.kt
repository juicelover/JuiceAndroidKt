package com.dream.juiceandroid

import com.dream.juiceandroid.developers.codelab.viewModel.DataBindingViewModel
import com.juiceandroid.base_lib.tool.keyvalue.KeyValueMgr
import com.juiceandroid.base_lib.tool.keyvalue.MmkvKeyValueMgr
import com.juiceandroid.base_lib.tool.network.RetrofitClient
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

/**
 * @Date :2022/7/5
 * @Description: koin 注入
 * @Author: 1244300796@qq.com
 */

/**
 * viewModel模块
 */
val viewModelModule = module {
    viewModel<DataBindingViewModel>()
}

/**
 * 本地数据模块
 */
val localModule = module {
    single<KeyValueMgr> { MmkvKeyValueMgr }
}

/**
 * 远程数据模块
 */
val remoteModule = module {
    single { RetrofitClient.getOkHttpClient() }
    single { RetrofitClient.getRetrofit(get(), "") }
}

/**
 * 数据仓库模块
 */
val repoModule = module {
}

/**
 * 当需要构建你的ViewModel对象的时候，就会在这个容器里进行检索
 */
val appModule = listOf(viewModelModule, localModule, repoModule, remoteModule)

