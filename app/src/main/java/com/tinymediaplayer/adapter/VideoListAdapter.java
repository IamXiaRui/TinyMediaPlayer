package com.tinymediaplayer.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.tinymediaplayer.R;
import com.tinymediaplayer.bean.VideoItemBean;
import com.tinymediaplayer.utils.StringUtil;

/**
 * Video列表适配器
 */
public class VideoListAdapter extends CursorAdapter {

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 生成新的 view
        View view = View.inflate(context, R.layout.item_video_list, null);
        ViewHolder hoder = new ViewHolder(view);
        view.setTag(hoder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 填充 view
        ViewHolder holder = (ViewHolder) view.getTag();

        //用游标得到一个Bean对象
        VideoItemBean videoItemBean = VideoItemBean.instanceFromCursor(cursor);

        holder.tvTitle.setText(videoItemBean.getTitle());
        holder.tvDuration.setText(StringUtil.formatDuration(videoItemBean.getDuration()));
        holder.tvSize.setText(Formatter.formatFileSize(context, videoItemBean.getSize()));
    }

    /**
     * 创建一个ViewHolder
     */
    private class ViewHolder {
        TextView tvTitle, tvDuration, tvSize;

        public ViewHolder(View root) {
            tvTitle = (TextView) root.findViewById(R.id.tv_video_item_title);
            tvDuration = (TextView) root.findViewById(R.id.tv_video_item_duration);
            tvSize = (TextView) root.findViewById(R.id.tv_video_item_size);
        }
    }
}
