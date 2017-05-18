package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apt5.propulsion.R;
import com.apt5.propulsion.object.Idea;

import java.util.List;

/**
 * Created by Van Quyen on 5/18/2017.
 */

public class DraftListViewAdapter extends BaseAdapter {
    private List<Idea> ideaList;
    private Context context;

    public DraftListViewAdapter(List<Idea> ideaList, Context context) {
        this.ideaList = ideaList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ideaList.size();
    }

    @Override
    public Object getItem(int position) {
        return ideaList.get(position);
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
            convertView = inflater.inflate(R.layout.item_draft, parent, false);

            //set up viewholder
            viewHolder = new ViewHolder();
            viewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.iv_draft_delete);
            viewHolder.imgEdit = (ImageView) convertView.findViewById(R.id.iv_draft_edit);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_draft_date);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_draft_title);

            // store the holder with the view
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(ideaList.get(position).getTitle());
        viewHolder.tvDate.setText(ideaList.get(position).getDate());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvDate;
        ImageView imgDelete;
        ImageView imgEdit;
    }
}
