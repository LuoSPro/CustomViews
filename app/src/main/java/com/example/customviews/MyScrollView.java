package com.example.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {


    private ScrollChangeListener mListener;

    public MyScrollView(Context context) {
        this(context,null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null){
            mListener.onScroll(l, t, oldl, oldt);
        }
    }

    public void setScrollChangeListener(ScrollChangeListener listener){
        this.mListener = listener;
    }


    public interface ScrollChangeListener{
        void onScroll(int l, int t, int oldl, int oldt);
    }
}
