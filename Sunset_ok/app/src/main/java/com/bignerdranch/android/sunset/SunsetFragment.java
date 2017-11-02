package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Administrator on 2017/10/11.
 */

public class SunsetFragment extends Fragment {
    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mSunViewR;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private boolean mIsSunset;
    private float mSunTop;
    private float mSunTopR;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);
        mSunViewR = view.findViewById(R.id.sun_r);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        mIsSunset = false;

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsSunset) {
                    sunset();
                    mIsSunset = true;
                } else {
                    sunRise();
                    mIsSunset = false;
                }
            }
        });

        return view;
    }

//    private void startAnimation() {
    private void sunset() {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        float sunYStartR = mSunViewR.getTop();
        float sunYEndR = mSkyView.getHeight() - mSunViewR.getHeight();

        mSunTop = sunYStart;
        mSunTopR = sunYStartR;

        ObjectAnimator heightAnimatorReflect = ObjectAnimator
                .ofFloat(mSunViewR, "y", sunYStartR, -sunYEndR)
                .setDuration(4000);
        heightAnimatorReflect.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet set = new AnimatorSet();
        set.play(heightAnimator)
                .with(sunsetSkyAnimator)
                .with(heightAnimatorReflect)
                .before(nightSkyAnimator);
        set.start();



//        sunsetSkyAnimator.start();
//        heightAnimator.start();
    }

    private void sunRise() {
        float sunYEnd = mSunTop;
        float sunYStart = mSkyView.getHeight();

        float sunYEndR = mSunTopR;
        float sunYStartR = mSkyView.getTop() - mSunViewR.getHeight();

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator heightAnimatorReflect = ObjectAnimator
                .ofFloat(mSunViewR, "y", sunYStartR, sunYEndR)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet set = new AnimatorSet();
        set.play(heightAnimator)
                .with(sunsetSkyAnimator)
                .with(heightAnimatorReflect)
                .after(nightSkyAnimator);
        set.start();

    }
}
