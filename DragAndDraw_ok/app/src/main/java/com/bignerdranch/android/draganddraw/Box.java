package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/10/9.
 */

public class Box implements Parcelable{

    public static final Creator<PointF> CREATOR = null;

    private PointF mOrigin;
    private PointF mCurrent;
    private int id;
    private float angle;
    private int id2;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }
    public PointF getCurrent() {
        return mCurrent;
    }
    public void setCurrent(PointF current) {
        mCurrent = current;
    }
    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
