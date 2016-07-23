package com.tinymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Video列表Bean
 */
public class VideoItemBean implements Serializable {

    private String title;
    private int duration;
    private int size;
    private String path;

    /**
     * @param cursor 游标
     * @return Video列表的Bean对象
     */
    public static VideoItemBean instanceFromCursor(Cursor cursor) {
        VideoItemBean videoItemBean = new VideoItemBean();
        if (cursor == null || cursor.getCount() == 0) {
            return videoItemBean;
        }

        // 解析cursor的内容
        videoItemBean.title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        videoItemBean.duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        videoItemBean.size = cursor.getInt(cursor.getColumnIndex(Media.SIZE));
        videoItemBean.path = cursor.getString(cursor.getColumnIndex(Media.DATA));

        return videoItemBean;
    }

    /**
     * 得到视频列表
     *
     * @param cursor 游标
     * @return 返回视频列表
     */
    public static ArrayList<VideoItemBean> instanceListFromCursor(Cursor cursor) {
        ArrayList<VideoItemBean> videoList = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return videoList;
        }
        //先移动到最开始的位置之前
        cursor.moveToPosition(-1);
        //循环遍历列表
        while (cursor.moveToNext()) {
            VideoItemBean videoItemBean = instanceFromCursor(cursor);
            videoList.add(videoItemBean);
        }
        return videoList;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }


}
