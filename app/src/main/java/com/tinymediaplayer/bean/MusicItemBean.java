package com.tinymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import com.tinymediaplayer.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/13.
 */
public class MusicItemBean implements Serializable {

    private String title;
    private String arties;
    private String path;

    /**
     * 从cursor生成一个对象
     */
    public static MusicItemBean instanceFromCursor(Cursor cursor) {
        MusicItemBean musicItemBean = new MusicItemBean();
        if (cursor == null || cursor.getCount() == 0) {
            return musicItemBean;
        }

        // 解析cursor的内容
        musicItemBean.title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
        musicItemBean.title = StringUtil.formatDisplayName(musicItemBean.title);
        musicItemBean.arties = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
        musicItemBean.path = cursor.getString(cursor.getColumnIndex(Media.DATA));

        return musicItemBean;
    }

    /**
     * 从cursor里解析出完整的播放列表
     */
    public static ArrayList<MusicItemBean> instanceListFromCursor(Cursor cursor) {
        ArrayList<MusicItemBean> musicList = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return musicList;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            MusicItemBean musicItemBean = instanceFromCursor(cursor);
            musicList.add(musicItemBean);
        }

        return musicList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArties() {
        return arties;
    }

    public void setArties(String arties) {
        this.arties = arties;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "title='" + title + '\'' +
                ", arties='" + arties + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
