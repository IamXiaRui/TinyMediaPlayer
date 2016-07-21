package com.tinymediaplayer.utils;

/**
 * 格式化String
 */
public class StringUtil {

    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;


    /**
     * 将时间戳转换为 01:01:01 或 01:01 的格式
     */
    public static String formatDuration(int duartion) {
        // 计算小时数
        int hour = duartion / HOUR;

        // 计算分钟数
        int min = duartion % HOUR / MIN;

        // 计算秒数
        int sec = duartion % MIN / SEC;

        // 生成格式化字符串
        if (hour == 0) {
            // 不足一小时 01：01
            return String.format("%02d:%02d", min, sec);
        } else {
            // 大于一小时 01:01:01
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
    }
}
