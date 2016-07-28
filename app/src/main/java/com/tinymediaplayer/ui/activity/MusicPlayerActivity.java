package com.tinymediaplayer.ui.activity;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.Toast;

import com.tinymediaplayer.R;
import com.tinymediaplayer.bean.MusicItemBean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 音乐播放界面
 */
public class MusicPlayerActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_musicplayer;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        ArrayList<MusicItemBean> musicItems = (ArrayList<MusicItemBean>) getIntent().getSerializableExtra("musicItems");
        int currentPosition = getIntent().getIntExtra("currentPosition", -1);
        MusicItemBean currentMusicItem = musicItems.get(currentPosition);
        Toast.makeText(this, currentMusicItem.getPath(), Toast.LENGTH_SHORT).show();
        //播放选中的音乐
        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(currentMusicItem.getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processClick(View v) {

    }
}
