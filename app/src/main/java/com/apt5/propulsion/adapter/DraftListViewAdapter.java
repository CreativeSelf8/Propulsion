package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apt5.propulsion.Keys;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        viewHolder.tvDate.setText(ideaList.get(position).getTime());

        viewHolder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pos = ideaList.get(position).getTitletime().toString();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("position", pos);
                intent.putExtra("data", bundle);
                intent.setAction(Keys.CHANGE_FRAGMENT_START_SEND);
                context.sendBroadcast(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvDate;
        ImageView imgDelete;
        ImageView imgEdit;
    }
}
