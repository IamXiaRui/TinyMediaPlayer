package com.tinymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import com.tinymediaplayer.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 音乐播放列表Bean
 */
public class MusicItemBean implements Serializable {

    private String title;
    private String artist;
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
        musicItemBean.title = StringUtil.formatDisplayName(cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
        musicItemBean.artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
        musicItemBean.path = cursor.getString(cursor.getColumnIndex(Media.DATA));

        return musicItemBean;
    }

    /**
     * 从cursor里解析出完整的播放列表
     */
    public static ArrayList<MusicItemBean> instanceListFromCursor(Cursor cursor) {
        ArrayList<MusicItemBean> musicLists = new ArrayList<MusicItemBean>();
        if (cursor == null || cursor.getCount() == 0) {
            return musicLists;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            MusicItemBean musicItemBean = instanceFromCursor(cursor);
            musicLists.add(musicItemBean);
        }

        return musicLists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
