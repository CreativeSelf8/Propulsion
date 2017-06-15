package com.apt5.propulsion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apt5.propulsion.R;
import com.bumptech.glide.Glide;


public class FullImageFragment extends Fragment {
    private ImageView imgFull;
    private String urlPhoto;

    public static FullImageFragment newInstance(String urlPhoto) {
        FullImageFragment f = new FullImageFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("URL_PHOTO", urlPhoto);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_full_image, container, false);

        urlPhoto = getArguments().getString("URL_PHOTO");

        imgFull = (ImageView) rootView.findViewById(R.id.img_full_image);
        Glide.with(getActivity()).load(urlPhoto).fitCenter().into(imgFull);

        return rootView;
    }
}
