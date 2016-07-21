package com.tinymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import java.io.Serializable;

/**
 * Video列表Bean
 */
public class VideoItemBean implements Serializable{

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
