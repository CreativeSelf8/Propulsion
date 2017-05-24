package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.apt5.propulsion.R;

import java.io.File;
import java.util.List;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class GridViewPhotoAdapter extends BaseAdapter {
    private List<String> bitmapList;
    private Context context;

    public GridViewPhotoAdapter(List<String> bitmapList, Context context) {
        this.bitmapList = bitmapList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateItem(String bitmap) {
        bitmapList.add(bitmap);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_gridphoto, parent, false);

            //set up viewholder
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_item_gridphoto);

            // store the holder with the view
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        File newFile = new File(bitmapList.get(position));
        Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
        viewHolder.imageView.setImageBitmap(myBitmap);
//        Glide.with(context).load(urlList.get(position)).into(viewHolder.imageView);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
