package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
import com.apt5.propulsion.object.Picture;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class GridViewPhotoAdapter extends RecyclerView.Adapter<GridViewPhotoAdapter.ViewHolder> {
    private ArrayList<Picture> bitmapList;
    private Activity context;
    private Realm realm;

    public GridViewPhotoAdapter(ArrayList<Picture> bitmapList, Activity context) {
        this.bitmapList = bitmapList;
        this.context = context;
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfiguration);
    }

    @Override
    public GridViewPhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gridphoto, viewGroup, false);
        ViewHolder holder = new ViewHolder(layoutView);

        return holder;
    }

    @Override
    public void onBindViewHolder(GridViewPhotoAdapter.ViewHolder viewHolder, final int position) {
        Bitmap myBitmap = CommonMethod.ByteArraytoBimap(bitmapList.get(position).getPicture());
        viewHolder.imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 320, 320, false));

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                RealmResults results = realm.where(Picture.class).equalTo("createTitletime", bitmapList.get(position).getCreateTitletime()).findAll();
                if (results.isValid()) {
                    results.deleteAllFromRealm();
                }
                realm.commitTransaction();
                bitmapList.remove(position);
                notifyItemChanged(position);
                notifyItemRangeChanged(position, bitmapList.size());
            }
        });

        viewHolder.setIsRecyclable(true);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (bitmapList == null) {
            return -1;
        } else
            return bitmapList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Activity context;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item_gridphoto);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
