package com.apt5.propulsion.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apt5.propulsion.R;
import com.apt5.propulsion.fragment.FullImageDialogFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.apt5.propulsion.ConstantVar.FULL_IMAGE;


public class GlideImageGridViewAdapter extends RecyclerView.Adapter<GlideImageGridViewAdapter.ViewHolder> {
    private ArrayList<String> urlList;
    private Context context;
    private FragmentManager fragmentManager;

    public GlideImageGridViewAdapter(ArrayList<String> urlList, Context context, FragmentManager fragmentManager) {
        this.urlList = urlList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public GlideImageGridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gridphoto, viewGroup, false);

        ViewHolder holder = new ViewHolder(layoutView);

        return holder;
    }

    @Override
    public void onBindViewHolder(GlideImageGridViewAdapter.ViewHolder viewHolder, final int i) {
        Glide.with(context).load(urlList.get(i)).fitCenter().into(viewHolder.imageView);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullImageDialogFragment dialogFragment = FullImageDialogFragment.newInstance(urlList, i);
                dialogFragment.show(fragmentManager, FULL_IMAGE);
            }
        });

        viewHolder.setIsRecyclable(false);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item_gridphoto);
        }
    }
}
