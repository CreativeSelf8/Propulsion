package com.apt5.propulsion.fragment;


import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.FullImageViewPagerAdapter;

import java.util.ArrayList;

import berlin.volders.rxdownload.RxDownloadManager;

import static com.apt5.propulsion.ConstantVar.FULL_IMAGE;

/**
 * Created by Van Quyen on 6/11/2017.
 */

public class FullImageDialogFragment extends DialogFragment {
    private ArrayList<String> listUrlPhoto = new ArrayList<>();
    private int currentPosition;
    private ViewPager vpFullImage;
    private FullImageViewPagerAdapter adapter;

    public FullImageDialogFragment() {
    }

    public static FullImageDialogFragment newInstance(ArrayList<String> listUrlPhoto, int currentPosition) {
        FullImageDialogFragment f = new FullImageDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putStringArrayList(FULL_IMAGE, listUrlPhoto);
        args.putInt("PHOTO_POSITION", currentPosition);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_full_image, container, false);
        listUrlPhoto = getArguments().getStringArrayList(FULL_IMAGE);
        currentPosition = getArguments().getInt("PHOTO_POSITION");
        ImageView imgExit = (ImageView) rootView.findViewById(R.id.img_exit);
        ImageView imgDownload = (ImageView) rootView.findViewById(R.id.img_download_file);
        vpFullImage = (ViewPager) rootView.findViewById(R.id.vp_image);
        adapter = new FullImageViewPagerAdapter(getChildFragmentManager(), listUrlPhoto);
        vpFullImage.setAdapter(adapter);
        vpFullImage.setCurrentItem(currentPosition);

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(vpFullImage.getCurrentItem());
            }
        });

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
        dialog.show();

        return dialog;
    }

    private void downloadImage(int currentPhotoPos) {
        String photoUrl = adapter.getCurrentUrl(currentPhotoPos);
        DownloadManager.Request request = RxDownloadManager.request(Uri.parse(photoUrl), "Propulsion-" + System.currentTimeMillis())
                .setDescription("Downloading...")
                .setMimeType("image/png");

        RxDownloadManager rxDownloadManager = RxDownloadManager.from(getActivity());
        rxDownloadManager.download(request);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
