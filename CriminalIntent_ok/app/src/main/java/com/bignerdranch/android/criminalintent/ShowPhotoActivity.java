package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ShowPhotoActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        File photoFile = (File) getIntent().getSerializableExtra(CrimeFragment.DIALOG_PHOTO);
        return ShowPhotoFragment.newInstance(photoFile);

    }
}
