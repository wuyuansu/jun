package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2017/10/31.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    PhotoPageFragment mFragment;
    @Override
    protected Fragment createFragment() {
        mFragment =  PhotoPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    public static Intent newIntent(Context context, Uri pageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(pageUri);
        return i;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mFragment.onBackPressed())
            return;

        super.onBackPressed();

    }
}
