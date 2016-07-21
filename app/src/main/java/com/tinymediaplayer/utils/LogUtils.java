package com.tinymediaplayer.utils;

import android.util.Log;

/**
 * 自定义Log工具类
 */
public class LogUtils {

    //自定义Log模式常量 true：调试模式 false：正常模式
    private static final boolean ENABLE = true;

    /**
     * 自定义debug模式Log
     *
     * @param tag 标签
     * @param msg 内容
     */
    public static void d(String tag, String msg) {
        if (ENABLE) {
            Log.d("XrLog --- " + tag, msg);
        }
    }

    /**
     * 自定义error模式Log
     *
     * @param tag 标签
     * @param msg 内容
     */
    public static void e(String tag, String msg) {
        if (ENABLE) {
            Log.e("XrLog --- " + tag, msg);
        }
    }

    /**
     * 自定义error模式Log
     *
     * @param cls 类
     * @param msg 内容
     */
    public static void e(Class cls, String msg) {
        if (ENABLE) {
            Log.e("XrLog --- " + cls.getSimpleName(), msg);
        }
    }
}
