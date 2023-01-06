package com.juiceandroid.base_lib.tool

import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import java.security.MessageDigest
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * 手机号正则表达式
 */
const val PHONE_NUMBER = "^((1[3-9])|2\\d)\\d{9}$"

/**
 * 判断指定字符串是否是手机号
 * @receiver 字符串
 * @return 是否是手机号
 */
fun String?.isPhoneNumber(): Boolean = if (this.isNullOrEmpty()) {
    false
} else {
    val p = Pattern.compile(PHONE_NUMBER)
    val m = p.matcher(this)
    m.matches()
}

/**
 * url正则表达式
 */
const val URL = "^(http|https|ftp)(://).+\$"

/**
 * 判断指定字符串是否是url
 * @receiver 字符串
 * @return 是否是url
 */
fun String?.isUrl(): Boolean = if (this.isNullOrEmpty()) {
    false
} else {
    val p = Pattern.compile(URL)
    val m = p.matcher(this)
    m.matches()
}

/**
 * 空oaid正则表达式
 */
const val EMPTY_OAID = "^[0\\-]*\$"

/**
 * 标点符号数组
 */
val PUNCTUATION_LIST = arrayOf(
    ',', '.', '?', '!', ';', '\"', '\'', '<', '>',
    '(', ')', '[', ']', '{', '}',
    '，', '。', '？', '！', '；', '“', '”', '‘', '’', '《', '》',
    '（', '）', '【', '】'
)

/**
 * 判断指定字符是标点符号
 * @receiver 字符
 * @return 是否是中文
 */
fun Char?.isPunctuation(): Boolean = if (this == null) {
    false
} else {
    this in PUNCTUATION_LIST
}

/**
 * 验证是否是邮箱
 */
fun String.isEmail(): Boolean { //邮箱判断正则表达式
    val pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
    val mc: Matcher = pattern.matcher(this)
    return mc.matches()
}

/**
 * RecyclerView中所用的ObservableArrayList替换所有内容的方法
 * @receiver ObservableArrayList
 * @param list 替换原内容的新内容
 * @param startIndex 新内容替换的起始位置，默认为0
 * @param endIndex 新内容替换的结束位置，默认为list.size
 */
fun <T : Any> ObservableArrayList<T>.replaceAll(
    list: List<out T>,
    startIndex: Int = 0,
    endIndex: Int = this.size
) {
    val oldSize = endIndex - startIndex
    val newSize = list.size
    when {
        oldSize < newSize -> {
            for (i in startIndex until endIndex) {
                if (this[i].javaClass != list[i - startIndex].javaClass) {
                    removeAt(i)
                    add(i, list[i - startIndex])
                } else {
                    if (this[i] != list[i - startIndex]) {
                        set(i, list[i - startIndex])
                    }
                }
            }
            for (i in endIndex until startIndex + newSize) {
                add(i, list[i - startIndex])
            }
        }
        oldSize == newSize -> {
            for (i in startIndex until endIndex) {
                if (this[i].javaClass != list[i - startIndex].javaClass) {
                    removeAt(i)
                    add(i, list[i - startIndex])
                } else {
                    if (this[i] != list[i - startIndex]) {
                        set(i, list[i - startIndex])
                    }
                }
            }
        }
        else -> {
            for (i in startIndex until startIndex + newSize) {
                if (this[i].javaClass != list[i - startIndex].javaClass) {
                    removeAt(i)
                    add(i, list[i - startIndex])
                } else {
                    if (this[i] != list[i - startIndex]) {
                        set(i, list[i - startIndex])
                    }
                }
            }
            for (i in endIndex - 1 downTo startIndex + newSize) {
                removeAt(i)
            }
        }
    }
}

/**
 * 对String进行MD5加密
 * @receiver String? 进行加密的内容
 * @return String 加密的结果
 */
fun String?.md5(): String {
    val bytes = MessageDigest.getInstance("md5").digest((this ?: "").toByteArray())
    val buffer = StringBuffer()
    for (b in bytes) {
        val numberStr = Integer.toHexString((b.toInt() and 0xff))
        if (numberStr.length == 1) {
            buffer.append("0")
        }
        buffer.append(numberStr)
    }
    return buffer.toString()
}

/**
 * MutableLiveData取值扩展方法
 * @receiver MutableLiveData对象
 * @param t 默认值
 * @return 取值结果
 */
fun <T> MutableLiveData<T>.get(t: T): T = this.value ?: t


/**
 * MutableLiveData初始化扩展方法
 * @receiver MutableLiveData
 * @param t 初始化值
 * @return 赋值后的MutableLiveData
 */
fun <T> MutableLiveData<T>.init(t: T) = MutableLiveData<T>().apply {
    value = t
}

/**
 * Fragment取值
 * @receiver Fragment
 * @param key 键
 * @param defaultValue 值
 * @return 值类型
 */
fun <T : Any?> Fragment.argument(key: String, defaultValue: Any? = null) =
    if (arguments === null) {
        defaultValue as T
    } else {
        arguments!![key] as? T ?: defaultValue as T
    }

/**
 * Activity取值
 * @receiver Activity
 * @param key 键
 * @param defaultValue 值
 * @return 值类型
 */
fun <T : Any?> FragmentActivity.argument(key: String, defaultValue: Any? = null) =
    if (intent.extras === null) {
        defaultValue as T
    } else {
        intent.extras!![key] as? T ?: defaultValue as T
    }

/**
 * 对象转成HashMap
 * SerializerFeature.WriteMapNullValue 保留为null的值
 */
fun Any?.toMap(): HashMap<String, Any?> {
    val toJSONString = JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    val parseObject = JSON.parseObject(toJSONString, Map::class.java)
    return parseObject as HashMap<String, Any?>
}
