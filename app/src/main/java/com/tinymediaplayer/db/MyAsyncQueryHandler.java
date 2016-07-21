package com.tinymediaplayer.db;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;

import com.tinymediaplayer.adapter.VideoListAdapter;

/**
 * 自定义MyAsyncQueryHandler
 */
public class MyAsyncQueryHandler extends AsyncQueryHandler {

    public MyAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        //得到一个adapter
        VideoListAdapter adapter = (VideoListAdapter) cookie;
        //并执行刷新操作
        adapter.swapCursor(cursor);
    }
}
