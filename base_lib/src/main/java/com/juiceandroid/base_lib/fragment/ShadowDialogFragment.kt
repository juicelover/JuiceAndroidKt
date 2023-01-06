package com.juiceandroid.base_lib.fragment

import android.app.Activity
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.juiceandroid.base_lib.R
import com.juiceandroid.base_lib.tool.DarkModeUtils


abstract class ShadowDialogFragment<VB : ViewDataBinding> : BaseDialogFragment<VB>() {
    /**
     * 动画，默认为渐入动画
     */
    override val animStyle: Int? = R.style.dialogNoAnim

    /**
     * 绑定DataBinding
     * @param inflater LayoutInflater
     * @param container 父view
     * @param savedInstanceState Bundle
     * @return 显示View
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingView =
            DataBindingUtil.inflate(requireActivity().layoutInflater, getLayoutId(), null, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window!!.setDecorFitsSystemWindows(false)
            val insetsController = View(context).windowInsetsController
            if (insetsController != null) {
                insetsController.show(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
                insetsController.show(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
            }
        } else {
            dialog?.window!!.decorView.systemUiVisibility =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
        }
        return bindingView.root
    }

    /**
     * 设置是否可取消以、动画以及显示位置
     * @param savedInstanceState Bundle
     * @return 对话框
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(cancelAble)
        dialog.setCanceledOnTouchOutside(outsideCancelAble)
        val lp = dialog.window?.attributes
        lp?.gravity = gravity
        dialog.window?.attributes = lp
        if (animStyle !== null) {
            dialog.window?.attributes?.windowAnimations = animStyle
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        dialog!!.window!!.setLayout(dialogWidth, dialogHeight)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val curr = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        DarkModeUtils.applySystemMode(requireContext())
        baseApplySkin(requireActivity())
    }

    override fun baseApplySkin(activity: Activity) {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (dialogWidth == 0 || dialogHeight == 0) {
            dismiss()
            return
        }
        dialog?.window!!.attributes.dimAmount = dimAmount
        initView()
        loadData(true)
    }

}