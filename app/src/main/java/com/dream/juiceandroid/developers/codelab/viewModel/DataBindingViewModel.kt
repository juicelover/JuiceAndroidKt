package com.dream.juiceandroid.developers.codelab.viewModel

import androidx.lifecycle.MutableLiveData
import com.juiceandroid.base_lib.base.BaseViewMode
import com.juiceandroid.base_lib.tool.init

/**
 * @author juice
 * @date 2023-01-09 13:51:53
 * @desc a vm for [com.dream.juiceandroid.developers.codelab.activity.DataBindingActivity].
 */
class DataBindingViewModel : BaseViewMode() {

    val name = MutableLiveData<String>().init("Juice")
    val lastName = MutableLiveData<String>().init("Don't cry")
    val likes = MutableLiveData<Int>().init(0)

    fun onLike() {
        likes.value = (likes.value ?: 0) + 1
    }
}