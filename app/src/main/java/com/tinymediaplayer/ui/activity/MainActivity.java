package com.tinymediaplayer.ui.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tinymediaplayer.R;
import com.tinymediaplayer.adapter.MainPagerAdapter;
import com.tinymediaplayer.ui.fragment.MusicListFragment;
import com.tinymediaplayer.ui.fragment.VideoListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TextView mVideoText, mMusicText;
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private MainPagerAdapter mPagerAdapter;
    private View mLineView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 初始化View
     */
    @Override
    public void initView() {
        mVideoText = (TextView) findViewById(R.id.tv_main_video);
        mMusicText = (TextView) findViewById(R.id.tv_main_music);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mLineView = findViewById(R.id.view_line);
    }

    /**
     * 绑定监听器与适配器
     */
    @Override
    public void initListener() {

        mVideoText.setOnClickListener(this);
        mMusicText.setOnClickListener(this);

        mFragments = new ArrayList<Fragment>();
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mPagerAdapter);

        //设置适配器
        mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        mFragments.add(new VideoListFragment());
        mFragments.add(new MusicListFragment());

        mPagerAdapter.notifyDataSetChanged();
        //高亮第一个标签
        updateTabs(0);

        //指示线的长度为Fragment总数目分之一
        mLineView.getLayoutParams().width = (getWindowManager().getDefaultDisplay().getWidth()) / mFragments.size();
        //重新计算大小 并刷新控件
        mLineView.requestLayout();
    }

    /**
     * Tab栏的点击事件
     *
     * @param v 点击的View
     */
    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_video:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_main_music:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    /**
     * 自定义ViewPager监听器
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //指示器起始位置 = position * 指示器宽度
            int startX = position * mLineView.getWidth();
            //指示器偏移位置 = 屏幕移动百分比 * 指示器宽度
            int offsetX = (int) (positionOffset * mLineView.getWidth());
             /*偏移位置 = 手指划过屏幕的像素 / pager个数
             int offsetX = positionOffsetPixels / mFragments.size();*/
            //指示器移动位置 = 指示器起始位置 + 指示器偏移位置
            int translationX = startX + offsetX;
            //指示器移动动画
            ViewHelper.setTranslationX(mLineView, translationX);
        }

        @Override
        public void onPageSelected(int position) {
            // 高亮选中页面对应的标签，并将其他的变暗
            updateTabs(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * 高亮position选中页面对应的标签，并将其他的变暗
     */
    private void updateTabs(int position) {
        updateTab(position, 0, mVideoText);
        updateTab(position, 1, mMusicText);
    }

    /**
     * 判断当前要处理的 tabPosition 是否是选中的 position，并修改tab的高亮状态
     */
    private void updateTab(int position, int tabPosition, TextView tab) {
        if (position == tabPosition) {
            // 更改颜色
            tab.setTextColor(Color.parseColor("#38B259"));
            // 更改大小
            ViewPropertyAnimator.animate(tab).scaleX(1.2f).scaleY(1.2f);
        } else {
            // 更改颜色
            tab.setTextColor(Color.parseColor("#66ffffff"));
            // 更改大小
            ViewPropertyAnimator.animate(tab).scaleX(1.0f).scaleY(1.0f);
        }
    }
}
