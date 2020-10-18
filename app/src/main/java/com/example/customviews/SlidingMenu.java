package com.example.customviews;

import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.core.view.ViewCompat;

public class SlidingMenu extends HorizontalScrollView {

    private static final String TAG = "SlidingMenu";

    private int mMenuMarginRight = 50;
    private int mMenuWidth;
    private View mMenuView;
    private View mContentView;

    //判断当前菜单栏是否打开
    private boolean isMenuOpen = false;

    //处理手势快速滑动
    private GestureDetector mGestureDetector;

    //判断当前事件是否被拦截
    private boolean isIntercept = false;
    private float contentScale;
    private float mContentScale1;

    public SlidingMenu(Context context) {
        this(context,null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(R.styleable.SlidingMenu);
        mMenuMarginRight = (int) ta.getDimension(R.styleable.SlidingMenu_menuMarginRight,dp2px(mMenuMarginRight));
        mMenuWidth = ScreenUtils.getScreenWidth(context) - mMenuMarginRight;
        ta.recycle();

        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    //添加手势事件处理
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //只关注快速滑动
            //当手势为左右快速滑动是，则切换状态
            if(Math.abs(velocityX) < Math.abs(velocityY)){
                //上下滑动的距离大于左右滑动的距离 代表当前为上下滑动
                //此时不处理
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            //当velocityX > 0  代表右滑  velocityX < 0 代表左滑
            if(isMenuOpen){
                //当菜单栏打开的时候，快速左滑，关闭菜单栏
                if(velocityX < 0){
                    closeMenu();
                    return true;
                }
            }else {
                if(velocityX > 0){
                    openMenu();
                    return true;
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    /**
     * 指定menuView和contentView的宽度
     * menuView为我们指定的宽度
     * contentView为全屏
     *
     * 原理：
     * 就是先获得SlidingMenu树下面的子View，然后给他们设置大小（通过LayoutParams这个类的对象，改变它里面的width属性）
     * 并判断是否满足使用要求
     *
     * 该方法在布局解析完成之后便会调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //获取到LinearLayout
        ViewGroup container = (ViewGroup) getChildAt(0);

        int childCount = container.getChildCount();
        //子View只能有两个  一个是菜单view 一个是contentview
        if(childCount != 2){
            throw new RuntimeException("childCount must have two!");
        }

        //设置MenuView的宽度
        mMenuView = container.getChildAt(0);

        ViewGroup.LayoutParams menuParams = mMenuView.getLayoutParams();
        menuParams.width = mMenuWidth;
        mMenuView.setLayoutParams(menuParams);

        //设置ContentView的宽度   这里为全屏显示
        mContentView = container.getChildAt(1);
        ViewGroup.LayoutParams contentParams = mContentView.getLayoutParams();
        contentParams.width = ScreenUtils.getScreenWidth(getContext());
        mContentView.setLayoutParams(contentParams);
    }

    /**
     * 随着滑动距离的变化，来缩放以及渐变 平移menuView 和contentView
     * 来达到视觉上的效果
     * @param l Current horizontal scroll origin.
     * @param t Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //以0.7为基数 缩放contentView
        mContentScale1 = (float) (0.7 + 0.3 * l*1f/mMenuView.getMeasuredWidth());
        //设置轴心坐标
        mContentView.setPivotX(0);
        mContentView.setPivotY(mContentView.getMeasuredHeight()/2);
        //设置缩放比例
        mContentView.setScaleX(mContentScale1);
        mContentView.setScaleY(mContentScale1);

        //给MenuView设置渐变 基数为0.5
        float menuAlpha = (float) (1 - 0.5*l*1f/mMenuView.getMeasuredWidth());
        //设置透明度
        mMenuView.setAlpha(menuAlpha);
        //给MenuView设置缩放
        float menuScale = (float) (1 - 0.3 * l * 1f/mMenuView.getMeasuredWidth());
        mMenuView.setScaleX(menuScale);
        mMenuView.setScaleY(menuScale);

        //设置menuView的抽屉效果（移动）
        mMenuView.setTranslationX(0.25f*l);
    }

    /**
     * 当在滑动的过程中还没关闭的时候抬起手，判断当前应该关闭menuView还是打开menuView
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //已被事件拦截
        if (isIntercept){
            return true;
        }
        //代表当前为手势快速滑动了
        if(mGestureDetector.onTouchEvent(ev)){
            return true;
        }
        if(ev.getAction() == MotionEvent.ACTION_UP){
            int scrollX = getScrollX();
            //当滑动的距离超过一半时，关闭菜单
            if(scrollX > mMenuView.getMeasuredWidth()/2){
                closeMenu();
            }else{
                openMenu();
            }
            //一定要消费事件
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(mMenuWidth,0);
    }

    private void closeMenu(){
        //用动画滑动到0
        smoothScrollTo(mMenuWidth,0);
        isMenuOpen = false;
    }

    private void openMenu(){
        smoothScrollTo(0,0);
        isMenuOpen = true;
    }

    //处理事件拦截   当菜单栏打开的时候，点击右侧内容页面 关闭菜单栏  且拦截内容页面的所有事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isIntercept = false;
        //当菜单栏打开的时候
        if(isMenuOpen){
            int x = (int) ev.getX();
            int y = (int) ev.getY();
//            Log.d(TAG, "onScrollChanged: contentScale ==> " + mContentScale1);
//            Log.d(TAG, "onInterceptTouchEvent: y ==> " + y);
            //上界的值
            float top = (ScreenUtils.getScreenHeight(getContext()) - mContentView.getMeasuredHeight()*mContentScale1)/2;
            float bottom = top + mContentView.getMeasuredHeight()*mContentScale1;
//            Log.d(TAG, "onInterceptTouchEvent: TOP ==> " + top);
//            Log.d(TAG, "onInterceptTouchEvent: bottom ==> " + bottom);
            //当触摸的位置在菜单栏之外的时候，关闭菜单栏
            if(x > mMenuView.getMeasuredWidth() && y > top && y < bottom){
                closeMenu();
                isIntercept = true;
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }


    private int dp2px(int dp){
        float scale = getResources().getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }
}
