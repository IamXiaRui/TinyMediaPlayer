package com.tinymediaplayer.ui.fragment;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tinymediaplayer.R;
import com.tinymediaplayer.adapter.VideoListAdapter;
import com.tinymediaplayer.bean.VideoItemBean;
import com.tinymediaplayer.db.MyAsyncQueryHandler;
import com.tinymediaplayer.ui.activity.VideoPlayerActivity;

/**
 * 视频列表
 */
public class VideoListFragment extends BaseFragment {

    private ListView mListView;
    private VideoListAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    public void initView() {
        mListView = (ListView) findViewById(R.id.lv_simple);
    }

    @Override
    public void initListener() {
        mAdapter = new VideoListAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void initData() {
        // 从MediaProvider里查询视频信息
        ContentResolver resolver = getActivity().getContentResolver();
        //新建一个自定义异步查询Handler
        AsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(resolver);
        //执行开始查询操作
        asyncQueryHandler.startQuery(0, mAdapter, Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);
    }

    @Override
    public void processClick(View v) {

    }

    /**
     * 列表监听器
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 获取被点击数据
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            VideoItemBean videoItemBean = VideoItemBean.instanceFromCursor(cursor);

            // 跳转到播放界面
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("videoItemBean", videoItemBean);
            startActivity(intent);
        }
    }
}
