package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9.
 */

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final double MAX_ANGLE = 1e-1;

    private Double mPreviousAngle;
    private float rotato;

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context) {
//       super(context);
        this(context, null);
    }

    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    action = "ACTION_DOWN";

                    mCurrentBox = new Box(current);
                    mCurrentBox.setId(event.getPointerId(0));
                    mBoxen.add(mCurrentBox);
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    if ((event.getPointerCount() >= 2) && (mCurrentBox != null)) {
                        // 2nd finger detected
                        PointF current2 = new PointF(event.getX(1), event.getY(1));
                        mCurrentBox.setOrigin(current2);
                        mCurrentBox.setId2(event.getPointerId(1));
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    action = "ACTION_MOVE";
                    // Update rectangle coordinates
                    if ((mCurrentBox != null) && (mCurrentBox.getId() == event.getPointerId(0))) {
                        mCurrentBox.setCurrent(current);
                        // 2nd finger
                        if (event.getPointerCount() >= 2) {
                            if (mCurrentBox.getId2() == event.getPointerId(1)) {
                                // 2nd finger detected
                                PointF current2 = new PointF(event.getX(1), event.getY(1));
                                mCurrentBox.setCurrent(current2);
                                // Angle calculation
                                mCurrentBox.setAngle(calcAngle(mCurrentBox.getOrigin(), mCurrentBox.getCurrent()));
                            }
                        }
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    action = "ACTION_UP";
                    mCurrentBox = null;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    action = "ACTION_CANCEL";
                    mCurrentBox = null;
                    break;
            }

        Log.i(TAG, action + " at x=" + current.x +
                ", y=" + current.y);

        return true;

    }

    public float calcAngle(PointF center, PointF target) {
        float angle = (float) Math.atan2(target.y - center.y, target.x - center.x);
        angle += Math.PI/2.0;
        // Translate to degrees
        angle = (float) Math.toDegrees(angle);

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

//    private static double angle(MotionEvent event) {
//        double deltaX = event.getX(0) - event.getX(1);
//        double deltaY = event.getY(0) - event.getY(1);
//
//        return  Math.atan2(deltaX, deltaY);
//    }
//
//    private static double clamp(double value, double min, double max) {
//        if (value < min)
//            return min;
//        if (value > max)
//            return max;
//
//        return value;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawPaint(mBackgroundPaint);

        for (Box box: mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            float centerX = (box.getOrigin().x + box.getCurrent().x)/2;
            float centerY = (box.getOrigin().y + box.getCurrent().y)/2;

            canvas.rotate(box.getAngle(), centerX, centerY);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.rotate(-1 * box.getAngle(), centerX, centerY);
        }


    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable superData = super.onSaveInstanceState();
        bundle.putParcelable("super_data", superData);
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) mBoxen);


        return bundle;
//        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
//        super.onRestoreInstanceState(state);
        Bundle bundle = (Bundle) state;
        mBoxen = bundle.getParcelableArrayList("data");
        Parcelable superData = bundle.getParcelable("super_data");
        super.onRestoreInstanceState(superData);

    }
}
