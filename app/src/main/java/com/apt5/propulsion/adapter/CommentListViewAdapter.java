package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
import com.apt5.propulsion.object.Comment;

import java.util.List;

/**
 * Created by Van Quyen on 5/16/2017.
 */

public class CommentListViewAdapter extends BaseAdapter {
    private List<Comment> commentList;
    private Context context;

    public CommentListViewAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_comment_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_item_comment_name);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_item_comment_content);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_item_comment_date);

            // store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(commentList.get(position).getAuthor());
        viewHolder.tvContent.setText(commentList.get(position).getContent());
        viewHolder.tvDate.setText(CommonMethod.convertToTime(commentList.get(position).getDate()));

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
    }
}
