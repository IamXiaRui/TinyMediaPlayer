package com.tinymediaplayer.db;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;

import com.tinymediaplayer.adapter.MusicListAdapter;

/**
 * 自定义MusicAsyncQueryHandler
 */
public class MusicAsyncQueryHandler extends AsyncQueryHandler {

    public MusicAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        //得到一个adapter
        MusicListAdapter adapter = (MusicListAdapter) cookie;
        //并执行刷新操作
        adapter.swapCursor(cursor);
    }
}
