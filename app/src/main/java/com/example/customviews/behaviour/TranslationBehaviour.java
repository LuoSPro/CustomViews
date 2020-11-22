package com.example.customviews.behaviour;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TranslationBehaviour extends FloatingActionButton.Behavior {
    private static final String TAG = "TranslationBehaviour";
    //关注垂直滚动，而且向上的时候是出来，向下是隐藏


    public TranslationBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    private boolean isOut = false;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        Log.d(TAG, "onNestedScroll: dyConsumed --> " + dyConsumed + "   dyUnconsumed --> " + dyUnconsumed);
        //向上滑动是出来，向下是隐藏
        if (dyConsumed > 0){
            //大于0，往上滑动
            if (!isOut){
                //滑动的距离，是Button的margin值，和它自身的高度，因为我们要让它完全隐藏，但是这样的效果不理想，所以要加一个标志位
                int translationY = ((CoordinatorLayout.LayoutParams)child.getLayoutParams()).bottomMargin + child.getMeasuredHeight();
                child.animate().translationY(translationY).setDuration(500).start();
                //优化：判断动画是否执行完毕

                isOut = true;
            }
        }else{
            //往下滑动
            if (isOut){
                child.animate().translationY(0).setDuration(1000).start();
                isOut = false;
            }
        }
    }
}
