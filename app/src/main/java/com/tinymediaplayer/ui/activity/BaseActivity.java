package com.tinymediaplayer.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.tinymediaplayer.R;
import com.tinymediaplayer.interfaces.UIInterface;
import com.tinymediaplayer.utils.LogUtils;

/**
 * 基本Activity
 * |--规范代码结构
 * |--提供公共方法
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener ,UIInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到布局文件
        setContentView(getLayoutId());

        //初始化View
        initView();

        //绑定监听器与适配器
        initListener();

        //初始化界面数据
        initData();

        //统一处理多界面相同按钮
        regCommonBtn();


    }

    /**
     * 在多个界面间都存在的按钮
     * 点击事件已经由BaseActivity处理
     * 那么将点击事件注册也统一处理掉
     */
    private void regCommonBtn() {
        View view = findViewById(R.id.iv_back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    /**
     * 对统一的按钮进行统一处理
     *
     * @param v 点击的View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                processClick(v);
                break;
        }
    }

    /**
     * 显示一个Toast
     *
     * @param msg 吐司内容
     */
    protected void BaseToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个Toast
     *
     * @param msgId 吐司内容
     */
    protected void BaseToast(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 打印一个Tag为类名的Log
     *
     * @param msg 内容
     */
    protected void logE(String msg) {
        LogUtils.e(getClass(), msg);
    }
}
