package com.tinymediaplayer.utils;

import android.database.Cursor;

/**
 * 游标查询工具类
 */
public class CursorUtil {
    private static final String TAG = "CursorUtil";

    /**
     * 打印cursor的所有内容
     */
    public static void printCursor(Cursor cursor) {
        LogUtils.e(TAG, "CursorUtil.printCursor:  查询到的数据个数为：" + cursor.getCount());
        while (cursor.moveToNext()) {
            LogUtils.e(TAG, "========================");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LogUtils.e(TAG, "CursorUtil.printCursor: name=" + cursor.getColumnName(i) + ";value=" + cursor.getString(i));
            }
        }
    }
}
