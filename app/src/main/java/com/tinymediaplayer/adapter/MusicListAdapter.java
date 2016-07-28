package com.tinymediaplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinymediaplayer.R;
import com.tinymediaplayer.bean.MusicItemBean;

/**
 * 音乐列表适配器
 */
public class MusicListAdapter extends CursorAdapter {

    public MusicListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public MusicListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public MusicListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 生成新的 view
        View view = View.inflate(context, R.layout.item_music_list, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 填充 view
        ViewHolder holder = (ViewHolder) view.getTag();

        MusicItemBean musicItemBean = MusicItemBean.instanceFromCursor(cursor);

        holder.titleText.setText(musicItemBean.getTitle());
        holder.singerText.setText(musicItemBean.getArtist());
    }

    private class ViewHolder {
        TextView titleText, singerText;

        public ViewHolder(View root) {
            titleText = (TextView) root.findViewById(R.id.tv_music_title);
            singerText = (TextView) root.findViewById(R.id.tv_music_singer);
        }
    }
}
