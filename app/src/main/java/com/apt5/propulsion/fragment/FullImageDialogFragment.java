package com.apt5.propulsion.fragment;


import android.app.Dialog;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;

import com.apt5.propulsion.R;
import com.bumptech.glide.Glide;

import berlin.volders.rxdownload.RxDownloadManager;

import static com.apt5.propulsion.ConstantVar.FULL_IMAGE;

/**
 * Created by Van Quyen on 6/11/2017.
 */

public class FullImageDialogFragment extends DialogFragment {
    private String URL_PHOTO = "";
    private static final String FOLDER_DIR = "Download";

    public FullImageDialogFragment() {
    }

    public static FullImageDialogFragment newInstance(String urlPhoto) {
        FullImageDialogFragment f = new FullImageDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(FULL_IMAGE, urlPhoto);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
        dialog.setContentView(R.layout.layout_full_image);

        URL_PHOTO = getArguments().getString(FULL_IMAGE);

        ImageView imgExit = (ImageView) dialog.findViewById(R.id.img_exit);
        ImageView imgDownload = (ImageView) dialog.findViewById(R.id.img_download_file);
        ImageView imgFull = (ImageView) dialog.findViewById(R.id.img_full_image);

        Glide.with(getActivity()).load(URL_PHOTO).fitCenter().into(imgFull);

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private void downloadImage() {
        DownloadManager.Request request = RxDownloadManager.request(Uri.parse(URL_PHOTO), "Propulsion-" + System.currentTimeMillis())
                .setDescription("Downloading...")
                .setMimeType("image/png");

        RxDownloadManager rxDownloadManager = RxDownloadManager.from(getActivity());
        rxDownloadManager.download(request);
    }
}
