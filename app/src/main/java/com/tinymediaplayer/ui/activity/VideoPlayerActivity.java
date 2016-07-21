package com.tinymediaplayer.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.tinymediaplayer.R;
import com.tinymediaplayer.bean.VideoItemBean;

/**
 * 视频播放界面
 */
public class VideoPlayerActivity extends BaseActivity {

    private VideoView mVideoView;
    private ImageView mPauseImage;

    @Override
    public int getLayoutId() {
        return R.layout.activity_videoplayer;
    }

    @Override
    public void initView() {
        mVideoView = (VideoView) findViewById(R.id.vv_videoplayer);
        mPauseImage = (ImageView) findViewById(R.id.iv_videoplayer_pause);
    }

    @Override
    public void initListener() {
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        mPauseImage.setOnClickListener(this);
    }

    @Override
    public void initData() {
        VideoItemBean videoItemBean = (VideoItemBean) getIntent().getSerializableExtra("videoItemBean");

        //设置视频的URL
        mVideoView.setVideoURI(Uri.parse(videoItemBean.getPath()));
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.iv_videoplayer_pause:
                //更换点击状态
                switchPauseStatus();
                break;
        }
    }

    /**
     * 切换视频播放/暂停状态
     */
    private void switchPauseStatus() {
        if (mVideoView.isPlaying()) {
            // 正在播放
            mVideoView.pause();
        } else {
            // 暂停状态
            mVideoView.start();
        }

        //更换点击图标
        updatePauseBtn();
    }

    /**
     * 根据当前播放状态，更改暂停按钮使用的图片
     */
    private void updatePauseBtn() {
        if (mVideoView.isPlaying()) {
            // 正在播放
            mPauseImage.setImageResource(R.drawable.video_pause_selector);
        } else {
            // 暂停状态
            mPauseImage.setImageResource(R.drawable.video_play_selector);
        }
    }

    /**
     * 视频准备事件监听器
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //必须准备完成后才能开启视频
            mVideoView.start();
            // 更新暂停按钮使用的图片
            updatePauseBtn();
        }
    }
}
