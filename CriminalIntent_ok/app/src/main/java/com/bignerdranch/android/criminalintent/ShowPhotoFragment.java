package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bignerdranch.android.criminalintent.util.PictureUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ShowPhotoFragment extends DialogFragment {

    private static final String SHOW_PHOTO = "photo";

    ImageView photo;

    public static ShowPhotoFragment newInstance(File f) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SHOW_PHOTO, f);

        ShowPhotoFragment showPhotoFragment = new ShowPhotoFragment();
        showPhotoFragment.setArguments(bundle);
        return showPhotoFragment;
}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File filePath = (File) getArguments().getSerializable(SHOW_PHOTO);
        Bitmap bitmap = PictureUtils.getScaledBitmap(filePath.getPath(), getActivity());

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_photo, null);
        photo = (ImageView) v.findViewById(R.id.photo_show);
        photo.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("photo")
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
