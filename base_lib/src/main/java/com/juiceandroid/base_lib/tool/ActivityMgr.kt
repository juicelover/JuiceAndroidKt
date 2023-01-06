package com.juiceandroid.base_lib.tool

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.util.*


object ActivityMgr {

    /**
     * activityStack : activity栈
     */
    private var activityStack: Stack<Activity> = Stack()

    var application: Application? = null

    /**
     * activityAount==0 则代表APP切到了后台
     */
    var activityAount: Int = 0

    /**
     * 初始化时与生命周期绑定
     * @param application Application
     */
    fun init(application: Application) {
        ActivityMgr.application = application
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                if (activityAount == 0) {
                    //从后台切回来
                }
                activityAount++;
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                activityAount--;
                if (activityAount == 0) {
                    //切到后台
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                removeActivity(activity)
            }
        })
    }

    /**
     * 入栈activity
     * @param activity 入栈的activity
     */
    fun addActivity(activity: Activity?) {
        activityStack.add(activity)
    }

    /**
     * 出栈activity
     * @param activity 出栈的activity
     */
    fun removeActivity(activity: Activity?) {
        activityStack.remove(activity)
    }

    /**
     * 获取首个页面Activity
     * @return 当前页面的Activity，找不到返回null
     */
    fun firstActivity(): Activity? =
        try {
            activityStack.firstElement()
        } catch (e: java.lang.Exception) {
            null
        }

    /**
     * 获取当前页面Activity
     * @return 当前页面的Activity，找不到返回null
     */
    fun currentActivity(): Activity? =
        try {
            activityStack.lastElement()
        } catch (e: java.lang.Exception) {
            null
        }


    /**
     * 获取上一页面Activity
     * @return 上一页面的Activity，找不到返回null
     */
    fun previousActivity(): FragmentActivity? =
        try {
            activityStack[activityStack.size - 2] as? FragmentActivity
        } catch (e: java.lang.Exception) {
            null
        }

    /**
     * 获取第一个Activity
     */
    fun getFirstActivity(): FragmentActivity? =
        try {
            activityStack.lastElement() as? FragmentActivity
        } catch (e: java.lang.Exception) {
            null
        }

    /**
     * 获取指定类名的Activity
     * @param cls Activity类
     * @return 栈中指定Activity列表
     */
    fun getActivity(cls: Class<*>): ArrayList<Activity> {
        val list: ArrayList<Activity> = arrayListOf()
        for (activity in activityStack) {
            if (activity.javaClass === cls) {
                list.add(activity)
            }
        }
        return list
    }

    /**
     * 获取栈中最上方的一个指定类名的Activity
     * @param cls Class<*> 指定获取的Activity类
     * @return Activity? 指定类的Activity，找不到返回null
     */
    fun getOneActivity(cls: Class<*>): Activity? {
        try {
            for (activity in activityStack) {
                if (activity.javaClass === cls) {
                    return activity
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 关闭栈中最后一个Activity
     */
    fun finishActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 关闭指定Activity
     * @param activity 指定关闭Activity
     */
    private fun finishActivity(activity: Activity) {
        activity.finish()
    }

    /**
     * 关闭指定的Activity
     * @param cls 指定关闭Activity类
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack) {
            if (activity.javaClass === cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        for (i in activityStack.lastIndex downTo 0) {
            activityStack[i]?.finish()
        }
        activityStack.clear()
    }

    /**
     * 结束所有Activity
     */
    fun moveToBackActivity() {
        for (i in activityStack.lastIndex downTo 0) {
            activityStack[i]?.moveTaskToBack(true)
        }
        activityStack.clear()
    }


    /**
     * 判断应用是否在前台
     * @param context Context对象，默认为ActivityMgr.getContext()
     * @return Boolean 应用是否在前台
     */
    fun isForeground(context: Context? = getContext()): Boolean {
        val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        if (tasks.isNotEmpty()) {
            val topActivity = tasks[0].topActivity
            if (topActivity!!.packageName == context.packageName) {
                return true
            }
        }
        return false
    }

    /**
     * 获取Context对象,返回初始化时传入的application对象
     */
    fun getContext(): Context = application!!.applicationContext
}
