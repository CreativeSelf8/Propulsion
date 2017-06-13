package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
import com.apt5.propulsion.object.Comment;
import com.apt5.propulsion.object.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class GridViewPhotoAdapter extends RecyclerView.Adapter<GridViewPhotoAdapter.ViewHolder> {
    private final OnItemLongClickListener listener;
    private ArrayList<Picture> bitmapList;
    private Activity context;

    public GridViewPhotoAdapter(ArrayList<Picture> bitmapList, Activity context, OnItemLongClickListener longClickListener) {
        this.bitmapList = bitmapList;
        this.context = context;
        this.listener = longClickListener;
    }

    @Override
    public GridViewPhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gridphoto, viewGroup, false);
        ViewHolder holder = new ViewHolder(layoutView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm.init(context);
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
                Realm realm = Realm.getInstance(realmConfiguration);
                realm.beginTransaction();
                bitmapList.get(i).deleteFromRealm();
                bitmapList.remove(i);
                realm.commitTransaction();
                GridViewPhotoAdapter.this.notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(GridViewPhotoAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(listener);
        Bitmap myBitmap = CommonMethod.ByteArraytoBimap(bitmapList.get(i).getPicture());
        viewHolder.imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 320, 320, false));

        viewHolder.setIsRecyclable(false);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (bitmapList == null)
        {
            return -1;
        }
        else
            return bitmapList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Activity context;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item_gridphoto);
        }

        public void bind(final OnItemLongClickListener longClickListener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onItemLongClick(getAdapterPosition());
                    return false;

                }
            });
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
