package com.example.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

public class QQSlidingMenu extends HorizontalScrollView {

    private static final String TAG = "SlidingMenu";

    private int mMenuMarginRight = 50;
    private int mMenuWidth;
    private View mMenuView;
    private View mContentView;
    private View mShadowView;

    //判断当前菜单栏是否打开
    private boolean isMenuOpen = false;

    //处理手势快速滑动
    private GestureDetector mGestureDetector;

    //判断当前事件是否被拦截
    private boolean isIntercept = false;
    private float mContentScale1;
    //方法一：(能实现功能，但在架构方面不行)
//    private ScaleChangeListener mScaleChangeListener;

    public QQSlidingMenu(Context context) {
        this(context,null);
    }

    public QQSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QQSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(R.styleable.SlidingMenu);
        mMenuMarginRight = (int) ta.getDimension(R.styleable.SlidingMenu_menuMarginRight,dp2px(mMenuMarginRight));
        mMenuWidth = ScreenUtils.getScreenWidth(context) - mMenuMarginRight;
        ta.recycle();

        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    //添加手势事件处理
    private OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //只关注快速滑动
            //当手势为左右快速滑动是，则切换状态
            if(Math.abs(velocityX) < Math.abs(velocityY)){
                //上下滑动的距离大于左右滑动的距离 代表当前为上下滑动
                //此时不处理，如果这里不返回回去，那么
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
                //关闭时，往右边快速滑动切换，打开菜单栏，
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
        //方法二：1.把布局单独提取出来：
        container.removeView(mContentView);
        ViewGroup.LayoutParams contentParams = mContentView.getLayoutParams();
        //方法二：2.然后在外面套一层阴影
        Context context = getContext();
        RelativeLayout contentContainer = new RelativeLayout(context);
        contentContainer.addView(mContentView);
        mShadowView = new View(context);
        mShadowView.setBackgroundColor(Color.parseColor("#55000000"));
        contentContainer.addView(mShadowView);
        //方法二：3.最后把容器放回原来的位置
        contentParams.width = ScreenUtils.getScreenWidth(getContext());
//        mContentView.setLayoutParams(contentParams);
        contentContainer.setLayoutParams(contentParams);
        container.addView(contentContainer);

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
        float scale = l * 1f / mMenuView.getMeasuredWidth();

        //方法一：通知外面改变阴影的透明度(能实现功能，但在架构方面不行)
//        if (mScaleChangeListener != null) {
//            mScaleChangeListener.onScaleChange(1 - scale);
//        }

        //方法二：控制阴影  0~1
        mShadowView.setAlpha(1 - scale);

        //这里不用再缩放了，因为这个效果不需要缩放效果了，只需要滑动了
//        //以0.7为基数 缩放contentView
//        mContentScale1 = (float) (0.7 + 0.3 * scale);
//        //设置轴心坐标
//        mContentView.setPivotX(0);
//        mContentView.setPivotY(mContentView.getMeasuredHeight()/2);
//        //设置缩放比例
//        mContentView.setScaleX(mContentScale1);
//        mContentView.setScaleY(mContentScale1);
//
//        //给MenuView设置渐变 基数为0.5
//        float menuAlpha = (float) (1 - 0.5*l*1f/mMenuView.getMeasuredWidth());
//        //设置透明度
//        mMenuView.setAlpha(menuAlpha);
//        //给MenuView设置缩放
//        float menuScale = (float) (1 - 0.3 * l * 1f/mMenuView.getMeasuredWidth());
//        mMenuView.setScaleX(menuScale);
//        mMenuView.setScaleY(menuScale);
//
//        //设置menuView的抽屉效果（移动）
//        //这里设置的是，两个布局重叠的部分，0.15就一点点，0.7就会重叠很大一部分
        mMenuView.setTranslationX(0.5f*l);
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
        //如果这里不调用scrollTo，那么我们刚开始的页面会出现，同时存在的情况，所以我们应该模拟滑动，使菜单栏呈现关闭的效果
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
            if(x > mMenuView.getMeasuredWidth()){
                //关闭菜单
                closeMenu();
                isIntercept = true;
                //子View不需要响应任何事件（点击和触摸），拦截子View事件
                //返回true，代表我会拦截子View的事件，但是会响应自己的onTouch事件，所以应该给onTouch事件的时候设置一个标记
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }


    private int dp2px(int dp){
        float scale = getResources().getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }

    //方法一：(能实现功能，但在架构方面不行)
//    public void setScaleChangeListener(ScaleChangeListener listener){
//        this.mScaleChangeListener = listener;
//    }
//
//    public interface ScaleChangeListener{
//        void onScaleChange(float scale);
//    }
}
