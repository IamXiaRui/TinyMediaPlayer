package com.tinymediaplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.nineoldandroids.view.ViewHelper;
import com.tinymediaplayer.R;
import com.tinymediaplayer.bean.VideoItemBean;
import com.tinymediaplayer.utils.StringUtil;

/**
 * 视频播放界面
 */
public class VideoPlayerActivity extends BaseActivity {

    private VideoView mVideoView;
    private ImageView mPauseImage;
    private TextView mTopTitleText;
    private MyBatteryBroadcastReceiver batteryReceiver;
    private ImageView mTopBatteryImage;
    private TextView mTopTimeText;

    //更新时间标志位
    private static final int UPDATE_SYSTEM_TIME = 0;
    //更新系统时间
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_SYSTEM_TIME:
                    updateSystemTime();
                    break;
            }
        }
    };
    private SeekBar mTopVoiceBar;
    private AudioManager mAudioManeger;
    private ImageView mMuteImage;
    private int oldVolume;
    private float mStartY;
    private int mStartVolume;
    private View mCoverView;
    private float mStartAlpha;
    private float mStartX;

    @Override
    public int getLayoutId() {
        return R.layout.activity_videoplayer;
    }

    /**
     * 初始化View
     */
    @Override
    public void initView() {
        //全局面板
        mVideoView = (VideoView) findViewById(R.id.vv_videoplayer);
        mCoverView = findViewById(R.id.view_player_cover);

        //顶部面板
        mTopTitleText = (TextView) findViewById(R.id.tv_top_title);
        mTopBatteryImage = (ImageView) findViewById(R.id.iv_top_battery);
        mTopTimeText = (TextView) findViewById(R.id.tv_top_time);
        mTopVoiceBar = (SeekBar) findViewById(R.id.sb_top_voice);
        mMuteImage = (ImageView) findViewById(R.id.iv_top_mute);
        //底部面板
        mPauseImage = (ImageView) findViewById(R.id.iv_videoplayer_pause);


    }

    @Override
    public void initListener() {
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        mPauseImage.setOnClickListener(this);

        //注册电量广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new MyBatteryBroadcastReceiver();
        registerReceiver(batteryReceiver, intentFilter);

        mTopVoiceBar.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
        mMuteImage.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        VideoItemBean videoItemBean = (VideoItemBean) getIntent().getSerializableExtra("videoItemBean");

        //设置视频的URL
        mVideoView.setVideoURI(Uri.parse(videoItemBean.getPath()));
        //初始化亮度为最亮
        ViewHelper.setAlpha(mCoverView, 0);
        //设置视频的标题
        mTopTitleText.setText(videoItemBean.getTitle());
        //动态更新系统时间
        updateSystemTime();
        //设置音量进度条总长度
        mAudioManeger = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVoice = mAudioManeger.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mTopVoiceBar.setMax(maxVoice);
        //设置当前音量
        mTopVoiceBar.setProgress(getCurrentVolume());
    }

    /**
     * 获得系统当前音量
     *
     * @return 系统当前音量
     */
    private int getCurrentVolume() {
        return mAudioManeger.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 非共同View的点击事件
     *
     * @param v 点击的View
     */
    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.iv_videoplayer_pause:
                //更换点击状态
                switchPauseStatus();
                break;
            case R.id.iv_top_mute:
                //点击静音状态
                switchMuteStatus();
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
     * 点击切换静音状态
     */
    private void switchMuteStatus() {
        //当前音量不为0的时候 记住当前音量 并静音
        if (getCurrentVolume() != 0) {
            oldVolume = getCurrentVolume();
            updateVolume(0);
        } else {
            //当前已经是静音状态时 恢复原电量
            updateVolume(oldVolume);
        }
    }

    /**
     * 更新音量
     *
     * @param volume 需要设置的音量
     */
    private void updateVolume(int volume) {
        mAudioManeger.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        mTopVoiceBar.setProgress(volume);
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
     * 动态更新系统时间
     */
    private void updateSystemTime() {
        //设置系统时间
        mTopTimeText.setText(StringUtil.formatTime());
        //用延时方式发送时间消息
        mHanlder.sendEmptyMessageDelayed(UPDATE_SYSTEM_TIME, 500);
    }

    /**
     * 触摸事件调节音量
     *
     * @param event 触摸事件
     * @return 是否消费
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //开始横坐标
                mStartX = event.getX();
                //开始纵坐标
                mStartY = event.getY();
                //开始音量
                mStartVolume = getCurrentVolume();
                mStartAlpha = ViewHelper.getAlpha(mCoverView);
                break;
            case MotionEvent.ACTION_MOVE:
                //手指当前位置横坐标
                float currentX = event.getX();
                //手指当前位置纵坐标
                float currentY = event.getY();
                //手指滑动位置
                float moveY = currentY - mStartY;
                //手指滑动距离占屏幕一半的百分比
                int halfScreenY = getWindowManager().getDefaultDisplay().getHeight() / 2;
                //移动的百分比
                float movePercent = moveY / halfScreenY;
                //屏幕宽度的一半
                int halfScreenX = getWindowManager().getDefaultDisplay().getWidth() / 2;
                if (event.getX() < halfScreenX) {
                    //更改亮度
                    changeLight(movePercent);
                } else {
                    //更改音量
                    changeVolume(movePercent);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据手指滑动百分比更改屏幕亮度
     *
     * @param movePercent 滑动百分比
     */
    private void changeLight(float movePercent) {
        //最终的亮度(透明度) = 初始亮度 + 变化的亮度
        float finalAlpha = mStartAlpha + movePercent;
        if (finalAlpha >= 0 && finalAlpha <= 1) {
            ViewHelper.setAlpha(mCoverView, finalAlpha);
        }
    }

    /**
     * 根据手指滑动百分比更改音量
     *
     * @param movePercent 滑动百分比
     */
    private void changeVolume(float movePercent) {
        //变化音量 = 手指滑动距离百分比 * 最大音量
        int offsetVolume = (int) (movePercent * mTopVoiceBar.getMax());
        //最终的音量 = 初始音量 + 变化的音量
        int finalVolume = mStartVolume + offsetVolume;
        //设置音量
        updateVolume(finalVolume);
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

    /**
     * 电量广播
     */
    private class MyBatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量百分比
            int level = intent.getIntExtra("level", 0);
            //根据百分比设置电量图片
            if (level < 10) {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_0);
            } else if (level < 20) {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_10);
            } else if (level < 40) {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_20);
            } else if (level < 60) {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_40);
            } else if (level < 80) {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_60);
            } else if (level < 100) {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_80);
            } else {
                mTopBatteryImage.setImageResource(R.drawable.ic_battery_100);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    /**
     * 音量进度条滑动监听
     */
    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //如果不是用户点击的 不处理
            if (!fromUser) {
                return;
            }
            //动态更改音量
            mAudioManeger.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
