package com.juiceandroid.base_lib.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.juiceandroid.base_lib.R
import com.juiceandroid.base_lib.databinding.DialogBlurBaseBinding
import com.juiceandroid.base_lib.tool.ActivityMgr
import com.juiceandroid.base_lib.tool.AppTool

abstract class BlurDialogFragment<VB : ViewDataBinding>: BaseDialogFragment<VB>(){

    /**
     * 动画，默认为无动画（模糊背景）
     */
    override val animStyle:Int? = R.style.dialogAlphaAnim
    /**
     * 真正的对话框的进入动画
     */
    open val animIn:Int?=null
    /**
     * 真正的对话框的退出动画
     */
    open val animOut:Int?=null
    /**
     * 模糊背景的ViewDataBinding
     */
    lateinit var mBaseBinding: DialogBlurBaseBinding

    /**
     * 设置监听以及模糊背景，并绑定DataBinding
     * @param inflater LayoutInflater
     * @param container 父view
     * @param savedInstanceState Bundle
     * @return 显示View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBaseBinding = DataBindingUtil.inflate(requireActivity().layoutInflater, R.layout.dialog_blur_base, null, false)

        mBaseBinding.container.setBackgroundColor(Color.argb((0xff*dimAmount).toInt(),0,0,0))
        mBaseBinding.container.gravity=gravity

        mBaseBinding.container.setOnClickListener { if (outsideCancelAble) exit() }

        bindingView = DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false)

        // content
        val params = RelativeLayout.LayoutParams(dialogWidth, dialogHeight)
        bindingView.root.layoutParams = params
        bindingView.root.isClickable=true
        bindingView.root.setOnClickListener { }
        val mContainer = mBaseBinding.root.findViewById(R.id.container) as RelativeLayout
        mContainer.addView(bindingView.root)

        dialog?.window!!.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        dialog?.setOnKeyListener{ _: DialogInterface, keyCode:Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK&&event.action== KeyEvent.ACTION_UP){
                if (cancelAble){
                    exit()
                }
                true
            }else{
                false
            }
        }

        return mBaseBinding.root
    }

    /**
     * 设置是否可取消以及动画
     * @param savedInstanceState Bundle
     * @return 对话框
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(cancelAble)
        dialog.setCanceledOnTouchOutside(outsideCancelAble)
        if (animStyle!==null){
            dialog.window?.attributes?.windowAnimations = animStyle
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        dialog!!.window!!.setLayout(
            AppTool.getScreenWidth(context?: ActivityMgr.currentActivity()!!),
            AppTool.showContentHeight)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (dialogWidth == 0 || dialogHeight == 0){
            dismiss()
            return
        }
        dialog?.window!!.attributes.dimAmount = dimAmount
        mBaseBinding.llRoot.background=generateBackground()
        initView()
        animIn?.let {
            val animation = AnimationUtils.loadAnimation(context, it)
            bindingView.root.startAnimation(animation)
        }
        loadData(true)
    }

    /**
     * 生成背景的模糊图
     * @return 背景图
     */
    private fun generateBackground(): Drawable? {
//        val activityView = dialog?.ownerActivity?.window?.decorView
//        val bmp = Bitmap.createBitmap(
//            AppTool.getScreenWidth(context?: ActivityMgr.currentActivity()!!),
//            AppTool.showContentHeight,
//            Bitmap.Config.ARGB_8888).apply {
//            val c = Canvas(this)
//            c.drawColor(Color.WHITE)
//            activityView?.draw(c)
//        }.blur(activity!!)
        return null
    }

    /**
     * 退出并按设定播放动画
     */
    override fun exit() {
        animOut?.let {
            val animation = AnimationUtils.loadAnimation(context, it)
            animation.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            bindingView.root.startAnimation(animation)
        }?:super.exit()
    }

}