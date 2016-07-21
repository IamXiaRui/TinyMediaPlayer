package com.tinymediaplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tinymediaplayer.R;
import com.tinymediaplayer.interfaces.UIInterface;
import com.tinymediaplayer.utils.LogUtils;

/**
 * 基本的Fragment
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener, UIInterface {

    private View baseView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = View.inflate(getActivity(), getLayoutId(), null);
        //初始化View
        initView();

        //绑定监听器与适配器
        initListener();

        //初始化界面数据
        initData();

        //统一处理多界面相同按钮
        regCommonBtn();

        return baseView;
    }

    /**
     * 返回View引用
     *
     * @param viewId View的Id
     * @return 返回View引用
     */
    protected View findViewById(int viewId) {
        return baseView.findViewById(viewId);
    }

    /**
     * 在多个界面间都存在的按钮
     * 点击事件已经由BaseActivity处理
     * 那么将点击事件注册也统一处理掉
     */
    private void regCommonBtn() {
        View view = findViewById(R.id.bt_back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    /**
     * 对统一的按钮进行统一处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                getFragmentManager().popBackStack();
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个Toast
     *
     * @param msgId 吐司内容
     */
    protected void BaseToast(int msgId) {
        Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
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
