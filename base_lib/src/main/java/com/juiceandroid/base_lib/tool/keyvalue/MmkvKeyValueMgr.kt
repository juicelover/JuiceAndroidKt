package com.juiceandroid.base_lib.tool.keyvalue

import android.os.Parcelable
import com.tencent.mmkv.MMKV


object MmkvKeyValueMgr : KeyValueMgr {

    private val valueManager by lazy {
        MMKV.mmkvWithID("value")
    }

    private val typeManager by lazy {
        MMKV.mmkvWithID("type")
    }

    override fun put(key: String, value: Any): Boolean {
        if (valueManager != null && typeManager != null) {
            when (value) {
                is Boolean -> {
                    typeManager.encode(key, "boolean")
                    return valueManager.encode(key, value)
                }
                is String -> {
                    typeManager.encode(key, "string")
                    return valueManager.encode(key, value)
                }
                is Int -> {
                    typeManager.encode(key, "int")
                    return valueManager.encode(key, value)
                }
                is Long -> {
                    typeManager.encode(key, "long")
                    return valueManager.encode(key, value)
                }
                is Float -> {
                    typeManager.encode(key, "float")
                    return valueManager.encode(key, value)
                }
                is Double -> {
                    typeManager.encode(key, "double")
                    return valueManager.encode(key, value)
                }
                is ByteArray -> {
                    typeManager.encode(key, "byteArray")
                    return valueManager.encode(key, value)
                }
                is Parcelable -> {
                    typeManager.encode(key, "parcelable")
                    return valueManager.encode(key, value)
                }
                is Set<*> -> {
                    typeManager.encode(key, "set")
                    return valueManager.encode(key, value as Set<String>)
                }

                else -> {
                    return false
                }
            }
        } else {
            return false
        }
    }

    override fun <T> get(key: String, defaultValue: T?): T? {
        when (typeManager.decodeString(key, null)) {
            "boolean" -> {
                return if (defaultValue == null) {
                    valueManager.decodeBool(key, false) as T
                } else {
                    if (defaultValue is Boolean) {
                        valueManager.decodeBool(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "string" -> {
                return if (defaultValue == null) {
                    valueManager.decodeString(key, null) as T
                } else {
                    if (defaultValue is String) {
                        valueManager.decodeString(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "int" -> {
                return if (defaultValue == null) {
                    valueManager.decodeInt(key, 0) as T
                } else {
                    if (defaultValue is Int) {
                        valueManager.decodeInt(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "long" -> {
                return if (defaultValue == null) {
                    valueManager.decodeLong(key, 0L) as T
                } else {
                    if (defaultValue is Long) {
                        valueManager.decodeLong(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "float" -> {
                return if (defaultValue == null) {
                    valueManager.decodeFloat(key, 0.0f) as T
                } else {
                    if (defaultValue is Float) {
                        valueManager.decodeFloat(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "double" -> {
                return if (defaultValue == null) {
                    valueManager.decodeDouble(key, 0.0) as T
                } else {
                    if (defaultValue is Double) {
                        valueManager.decodeDouble(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "byteArray" -> {
                return if (defaultValue == null) {
                    valueManager.decodeBytes(key, null) as T
                } else {
                    if (defaultValue is ByteArray) {
                        valueManager.decodeBytes(key, defaultValue) as T
                    } else {
                        defaultValue
                    }
                }
            }
            "set" -> {
                return if (defaultValue == null) {
                    valueManager.decodeStringSet(key, null) as T
                } else {
                    if (defaultValue is Set<*>) {
                        valueManager.decodeStringSet(key, defaultValue as Set<String>) as T
                    } else {
                        defaultValue
                    }
                }
            }
            else -> {
                return defaultValue
            }
        }
        return defaultValue
    }

    override fun <T : Parcelable> getObject(key: String, tClass: Class<T>, defaultValue: T?): T? {
        return if (typeManager.decodeString("key", null) == "parcelable") {
            valueManager.decodeParcelable(key, tClass, defaultValue)
        } else {
            defaultValue
        }
    }


    override fun remove(key: String) {
        typeManager.remove(key)
        valueManager.remove(key)
    }

    override fun contains(key: String): Boolean = typeManager.contains(key)

    override fun clear() {
        typeManager.clearAll()
        valueManager.clearAll()
    }

}