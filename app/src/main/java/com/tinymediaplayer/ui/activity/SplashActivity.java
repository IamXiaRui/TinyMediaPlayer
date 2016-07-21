package com.tinymediaplayer.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.tinymediaplayer.R;

/**
 * APP启动引导界面
 */
public class SplashActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
    }

    @Override
    public void processClick(View v) {

    }

    /**
     * 延时跳转
     */
    @Override
    protected void onResume() {
        super.onResume();
        //延时跳转主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
