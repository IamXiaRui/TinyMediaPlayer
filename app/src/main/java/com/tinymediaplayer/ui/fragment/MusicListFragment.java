package com.tinymediaplayer.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tinymediaplayer.R;
import com.tinymediaplayer.adapter.MusicListAdapter;
import com.tinymediaplayer.bean.MusicItemBean;
import com.tinymediaplayer.db.MusicAsyncQueryHandler;
import com.tinymediaplayer.ui.activity.MusicPlayerActivity;

import java.util.ArrayList;

/**
 * 音乐列表界面
 */
public class MusicListFragment extends BaseFragment {

    private ListView listView;
    private MusicListAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_music;
    }

    @Override
    public void initView() {
        listView = (ListView) findViewById(R.id.lv_simple);
    }

    @Override
    public void initListener() {
        mAdapter = new MusicListAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnMusicItemClickListener());
    }

    @Override
    public void initData() {
        // 从MediaProvider查询数据
        ContentResolver resolver = getActivity().getContentResolver();

        AsyncQueryHandler asyncQueryHandler = new MusicAsyncQueryHandler(resolver);
        asyncQueryHandler.startQuery(1, mAdapter, Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);
    }

    @Override
    public void processClick(View v) {

    }

    /**
     * 音乐列表点击事件监听
     */
    private class OnMusicItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 获取被点击的数据
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            ArrayList<MusicItemBean> musicItems = MusicItemBean.instanceListFromCursor(cursor);

            // 跳转到播放界面
            Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
            intent.putExtra("musicItems", musicItems);
            intent.putExtra("currentPosition", position);
            startActivity(intent);
        }
    }
}
