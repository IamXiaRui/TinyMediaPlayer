package com.tinymediaplayer.ui.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tinymediaplayer.R;
import com.tinymediaplayer.bean.VideoItemBean;
import com.tinymediaplayer.ui.view.MyVideoView;
import com.tinymediaplayer.utils.StringUtil;

import java.util.ArrayList;

/**
 * 视频播放界面
 */
public class VideoPlayerActivity extends BaseActivity {

    /*========= 基本控件变量 =========*/
    private MyVideoView mVideoView;
    private ImageView mPauseImage, mTopBatteryImage, mMuteImage, mPreImage, mNextImage, mFullImage;
    private TextView mTopTitleText, mTopTimeText, mYetTimeText, mAllTimeText;
    private SeekBar mPlayVoiceBar, mPlayTimeBar;
    private View mCoverView;
    private LinearLayout mTopLayout, mBottomLayout;

    /*========= 其他变量 =========*/
    //电池广播
    private VideoBatteryBroadcastReceiver batteryReceiver;
    //音频管理者
    private AudioManager mAudioManeger;
    //滑动相关变量
    private int oldVolume, mStartVolume, mCurrentVideoPosition;
    private float mStartX, mStartY, mStartAlpha;
    //进度条监听器
    private VideoOnSeekBarChangeListener seekBarChangeListener;
    //视频列表
    private ArrayList<VideoItemBean> mVideoList;
    //手势监听器
    private GestureDetector mGestureDetector;

    /*========= 标志位变量 =========*/
    //更新系统时间标志位
    private static final int UPDATE_SYSTEM_TIME = 0;
    //更新播放时间标志位
    private static final int UPDATE_PLAYER_TIME = 1;
    //播放控制面板是否显示标志位
    private static boolean CONTROL_PANEL_IS_SHOWING = true;
    //隐藏控制面板标志位
    private static final int HIDE_CONTROL_PANEL = 2;

    /**
     * 处理延时消息
     */
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_SYSTEM_TIME:
                    //更新系统时间
                    updateSystemTime();
                    break;
                case UPDATE_PLAYER_TIME:
                    //更新播放时间
                    updatePlayerTime();
                    break;
                case HIDE_CONTROL_PANEL:
                    //隐藏播放控制面板
                    hideControlPanel();
                    break;
            }
        }
    };

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
        mVideoView = (MyVideoView) findViewById(R.id.vv_videoplayer);
        mCoverView = findViewById(R.id.view_player_cover);
        mTopLayout = (LinearLayout) findViewById(R.id.ll_video_top);
        mBottomLayout = (LinearLayout) findViewById(R.id.ll_video_bottom);

        //顶部面板
        mTopTitleText = (TextView) findViewById(R.id.tv_top_title);
        mTopBatteryImage = (ImageView) findViewById(R.id.iv_top_battery);
        mTopTimeText = (TextView) findViewById(R.id.tv_top_time);
        mPlayVoiceBar = (SeekBar) findViewById(R.id.sb_top_voice);
        mMuteImage = (ImageView) findViewById(R.id.iv_top_mute);

        //底部面板
        mPauseImage = (ImageView) findViewById(R.id.iv_bottom_pause);
        mYetTimeText = (TextView) findViewById(R.id.tv_bottom_yettime);
        mPlayTimeBar = (SeekBar) findViewById(R.id.sb_bottom_playertime);
        mAllTimeText = (TextView) findViewById(R.id.tv_bottom_alltime);
        mPreImage = (ImageView) findViewById(R.id.iv_bottom_pre);
        mNextImage = (ImageView) findViewById(R.id.iv_bottom_next);
        mFullImage = (ImageView) findViewById(R.id.iv_bottom_full);

    }

    /**
     * 绑定相关监听器、适配器
     */
    @Override
    public void initListener() {
        mVideoView.setOnPreparedListener(new VideoOnPreparedListener());
        mVideoView.setOnCompletionListener(new VideoOnCompletionListener());
        mPauseImage.setOnClickListener(this);
        mPreImage.setOnClickListener(this);
        mNextImage.setOnClickListener(this);
        mFullImage.setOnClickListener(this);
        //手势监听器
        mGestureDetector = new GestureDetector(this, new ControlorSimpleOnGestureListener());

        //注册电量广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new VideoBatteryBroadcastReceiver();
        registerReceiver(batteryReceiver, intentFilter);

        seekBarChangeListener = new VideoOnSeekBarChangeListener();
        mPlayVoiceBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mPlayTimeBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mMuteImage.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        Uri videoUri = getIntent().getData();
        if (videoUri != null) {
            //外部调用
            mVideoView.setVideoURI(videoUri);
            mPreImage.setEnabled(false);
            mNextImage.setEnabled(false);
            mTopTitleText.setText(videoUri.getPath());
        } else {
            //内部调用
            mVideoList = (ArrayList<VideoItemBean>) getIntent().getSerializableExtra("videoList");
            mCurrentVideoPosition = getIntent().getIntExtra("position", -1);
            //播放选中的视频
            playItemVideo();
        }
        //初始化亮度为最亮
        ViewHelper.setAlpha(mCoverView, 0);
        //动态更新系统时间
        updateSystemTime();
        //设置音量进度条总长度
        mAudioManeger = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVoice = mAudioManeger.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mPlayVoiceBar.setMax(maxVoice);
        //设置当前音量
        mPlayVoiceBar.setProgress(getCurrentVolume());
        //初始隐藏控制面板
        initHideControlPanel();
    }


    /**
     * 初识隐藏控制面板
     */
    private void initHideControlPanel() {
        /** getMeasuredHeight:
         优点：只要执行了measure方法之后就可以获取到高度。
         缺点：在嵌套使用布局的情况下，有可能获取不到正确宽高。
         getHeight:
         优点：只要能获取到宽高就必定是准确的
         缺点：执行了onLayout方法之后才能获取到高度，在onCreate过程无法获取到高度。*/

        //得到顶部的高度: getMeasuredHeight
        mTopLayout.measure(0, 0);
        ViewPropertyAnimator.animate(mTopLayout).translationY(-mTopLayout.getMeasuredHeight());

        //得到底部的高度: getHeight
        mBottomLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mBottomLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewPropertyAnimator.animate(mBottomLayout).translationY(mBottomLayout.getMeasuredHeight());
            }
        });
    }

    /**
     * 播放选中的视频
     */
    private void playItemVideo() {
        VideoItemBean videoItemBean = mVideoList.get(mCurrentVideoPosition);
        //设置视频的URL
        mVideoView.setVideoURI(Uri.parse(videoItemBean.getPath()));
        //设置视频的标题
        mTopTitleText.setText(videoItemBean.getTitle());
        //设置当前视频的总长度
        mAllTimeText.setText(StringUtil.formatDuration(videoItemBean.getDuration()));
        //设置播放进度条最大长度
        mPlayTimeBar.setMax(videoItemBean.getDuration());
        //切换按钮样式
        updatePreAndNextBtn();
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
            case R.id.iv_bottom_pause:
                //更换点击状态
                switchPauseStatus();
                break;
            case R.id.iv_bottom_pre:
                //点击播放上一个视频
                if (mCurrentVideoPosition != 0) {
                    mCurrentVideoPosition--;
                    playItemVideo();
                }
                //切换按钮样式
                updatePreAndNextBtn();
                break;
            case R.id.iv_bottom_next:
                //点击播放下一个视频
                if (mCurrentVideoPosition != mVideoList.size() - 1) {
                    mCurrentVideoPosition++;
                    playItemVideo();
                }
                //切换按钮样式
                updatePreAndNextBtn();
                break;
            case R.id.iv_top_mute:
                //点击静音状态
                switchMuteStatus();
                break;
            case R.id.iv_bottom_full:
                //点击切换全屏状态
                mVideoView.switchFullScreen();
                //更新全屏按钮样式
                updateFullScreenBtn();
                break;
        }
    }

    /**
     * 更新全屏按钮样式
     */
    private void updateFullScreenBtn() {
        if (mVideoView.isFullScreen()) {
            mFullImage.setImageResource(R.drawable.video_defaultscreen_selector);
        } else {
            mFullImage.setImageResource(R.drawable.video_fullscreen_selector);
        }
    }

    /**
     * 更新上一个与下一个的按钮
     */
    private void updatePreAndNextBtn() {
        mPreImage.setEnabled(mCurrentVideoPosition != 0);
        mNextImage.setEnabled(mCurrentVideoPosition != mVideoList.size() - 1);
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
        mPlayVoiceBar.setProgress(volume);
    }

    /**
     * 根据当前播放状态，更改暂停按钮使用的图片
     */
    private void updatePauseBtn() {
        if (mVideoView.isPlaying()) {
            // 正在播放
            mPauseImage.setImageResource(R.drawable.video_pause_selector);
            //用延时方式发送时间消息
            mHanlder.sendEmptyMessageDelayed(UPDATE_PLAYER_TIME, 500);
        } else {
            // 暂停状态
            mPauseImage.setImageResource(R.drawable.video_play_selector);
            //停止时间更新Handler
            mHanlder.removeMessages(UPDATE_PLAYER_TIME);
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
        //先给系统自带的手势监听处理
        mGestureDetector.onTouchEvent(event);
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
        int offsetVolume = (int) (movePercent * mPlayVoiceBar.getMax());
        //最终的音量 = 初始音量 + 变化的音量
        int finalVolume = mStartVolume + offsetVolume;
        //设置音量
        updateVolume(finalVolume);
    }


    /**
     * 视频准备事件监听器
     */
    private class VideoOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //必须准备完成后才能开启视频
            mVideoView.start();
            // 更新暂停按钮使用的图片
            updatePauseBtn();
            //更新已播放时间
            updatePlayerTime();
            //全屏按钮初始化
            mFullImage.setImageResource(R.drawable.video_fullscreen_selector);
        }
    }

    /**
     * 视频播放完成的监听器
     */
    private class VideoOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //避免系统时间错误 直接更新已时间为视频长度
            updateBarPosition(mp.getDuration());
            //更新播放/暂停图标
            updatePauseBtn();
            //停止时间更新Handler
            mHanlder.removeMessages(UPDATE_PLAYER_TIME);
        }
    }

    /**
     * 更新已播放时间
     */
    private void updatePlayerTime() {
        int currentPosition = mVideoView.getCurrentPosition();
        //更新播放进度条的位置
        updateBarPosition(currentPosition);
        //发送延迟消息
        mHanlder.sendEmptyMessageDelayed(UPDATE_PLAYER_TIME, 500);
    }

    /**
     * 更新播放进度条的位置
     *
     * @param currentPosition 当前位置
     */
    private void updateBarPosition(int currentPosition) {
        //更新已播放时间进度
        mYetTimeText.setText(StringUtil.formatDuration(currentPosition));
        //设置当前进度条
        mPlayTimeBar.setProgress(mVideoView.getCurrentPosition());
    }

    /**
     * 电量广播
     */
    private class VideoBatteryBroadcastReceiver extends BroadcastReceiver {
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
    private class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //如果不是用户点击的 不处理
            if (!fromUser) {
                return;
            }
            switch (seekBar.getId()) {
                case R.id.sb_top_voice:
                    //动态更改音量
                    mAudioManeger.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    break;
                case R.id.sb_bottom_playertime:
                    //动态更改播放进度
                    mVideoView.seekTo(progress);
                    //重新更新时间进度
                    updateBarPosition(progress);
                    break;
            }
        }

        /**
         * 开始滑动进度条
         *
         * @param seekBar 进度条
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //取消发送隐藏播放控制面板消息
            mHanlder.removeMessages(HIDE_CONTROL_PANEL);
        }

        /**
         * 停止滑动进度条
         *
         * @param seekBar 进度条
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //继续发送隐藏播放控制面板消息
            mHanlder.sendEmptyMessageDelayed(HIDE_CONTROL_PANEL, 4000);
        }
    }

    /**
     * 系统自带手势处理监听器
     */
    private class ControlorSimpleOnGestureListener extends SimpleOnGestureListener {

        /**
         * 单击事件
         *
         * @param e 事件类型
         * @return 是否处理
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //判断控制面板是否显示
            if (CONTROL_PANEL_IS_SHOWING) {
                //显示状态 隐藏播放控制面板
                hideControlPanel();
            } else {
                //隐藏状态 显示播放控制面板
                showControlPanel();
                //发送隐藏播放控制面板消息
                mHanlder.sendEmptyMessageDelayed(HIDE_CONTROL_PANEL, 4000);
            }
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击全屏事件
         *
         * @param e 事件类型
         * @return 是否处理
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mVideoView.switchFullScreen();
            updateFullScreenBtn();
            return super.onDoubleTap(e);
        }

        /**
         * 长按播放/暂停
         *
         * @param e 事件类型
         */
        @Override
        public void onLongPress(MotionEvent e) {
            switchPauseStatus();
            super.onLongPress(e);
        }
    }

    /**
     * 显示状态 隐藏播放控制面板
     */
    private void hideControlPanel() {
        ViewPropertyAnimator.animate(mTopLayout).translationY(-mTopLayout.getHeight());
        ViewPropertyAnimator.animate(mBottomLayout).translationY(mBottomLayout.getHeight());
        CONTROL_PANEL_IS_SHOWING = false;
    }

    /**
     * 隐藏状态 显示播放控制面板
     */
    private void showControlPanel() {
        ViewPropertyAnimator.animate(mTopLayout).translationY(0);
        ViewPropertyAnimator.animate(mBottomLayout).translationY(0);
        CONTROL_PANEL_IS_SHOWING = true;
    }
}
