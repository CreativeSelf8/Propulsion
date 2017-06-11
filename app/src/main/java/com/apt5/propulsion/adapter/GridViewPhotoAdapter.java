package com.apt5.propulsion.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apt5.propulsion.R;

import java.io.File;
import java.util.List;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class GridViewPhotoAdapter extends RecyclerView.Adapter<GridViewPhotoAdapter.ViewHolder> {
    private final OnItemLongClickListener listener;
    private List<String> bitmapList;
    private Context context;

    public GridViewPhotoAdapter(List<String> bitmapList, Context context, OnItemLongClickListener longClickListener) {
        this.bitmapList = bitmapList;
        this.context = context;
        this.listener = longClickListener;
    }

    @Override
    public GridViewPhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gridphoto, viewGroup, false);

        ViewHolder holder = new ViewHolder(layoutView);

        return holder;
    }

    @Override
    public void onBindViewHolder(GridViewPhotoAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(listener);

        File newFile = new File(bitmapList.get(i));
        Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
        viewHolder.imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 320, 320, false));

        viewHolder.setIsRecyclable(false);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ImageView imageView;

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

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
