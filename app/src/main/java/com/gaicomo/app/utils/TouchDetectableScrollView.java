package com.gaicomo.app.utils;


import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TouchDetectableScrollView extends NestedScrollView {

    private static final String TAG = "TouchDetectableScrollVi";
    OnMyScrollChangeListener myScrollChangeListener;

    public TouchDetectableScrollView(Context context) {
        super(context);
    }

    public TouchDetectableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchDetectableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (myScrollChangeListener!=null)
        {
            if (t>oldt)
            {
//                myScrollChangeListener.onScrollDown();
            }
            else if (t<oldt){
//                myScrollChangeListener.onScrollUp();
            }
            View view = (View) getChildAt(getChildCount()-1);
            int diff = (view.getBottom()-(getHeight()+getScrollY()));
            Log.d(TAG, "onScrollChanged: "+ diff);
            if (diff <= 500) {
                myScrollChangeListener.onBottomReached();
            }
        }
    }

    public OnMyScrollChangeListener getMyScrollChangeListener() {
        return myScrollChangeListener;
    }

    public void setMyScrollChangeListener(OnMyScrollChangeListener myScrollChangeListener) {
        this.myScrollChangeListener = myScrollChangeListener;
    }

    public interface OnMyScrollChangeListener
    {
//        public void onScrollUp();
//        public void onScrollDown();
        public void onBottomReached();
    }
}