package com.example.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class LetterSideBar extends View {

    private Paint mPaint;
    private Paint mSelectedPaint;
    private int mLetterColor;
    private int mSelectedLetterColor;
    private float mLetterSize = 12;
    private char[] mLetters = new char[]{'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'};
    //当前触摸的位置字母
    private char mCurTouchLetter;
    private LetterTouchListener mLetterTouchListener;

    public LetterSideBar(Context context) {
        this(context, null);
    }

    public LetterSideBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterSideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterSideBar);
        mLetterColor = typedArray.getColor(R.styleable.LetterSideBar_LetterColor, mLetterColor);
        mSelectedLetterColor = typedArray.getColor(R.styleable.LetterSideBar_SelectedLetterColor, mSelectedLetterColor);
        mLetterSize = typedArray.getDimensionPixelSize(R.styleable.LetterSideBar_LetterSize, (int) mLetterSize);
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint = getPaint(mLetterColor);
        mSelectedPaint = getPaint(mSelectedLetterColor);
    }

    private Paint getPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //这里设置的是px，如果要设置的是sp，就要把它转成px
        paint.setTextSize(mLetterSize);
        paint.setColor(color);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算指定宽度，高度可以直接获取
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //计算指定宽度（考虑情况）：左右padding+字母宽度(取决于Paint的大小)
        //获得字体的宽度
        int textWidth = (int) mPaint.measureText("A");
        int width = getPaddingLeft() + getPaddingRight() + textWidth;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画26个字母
        int itemHeight = (getHeight() - getPaddingTop() - getPaddingBottom()) / mLetters.length;
        for (int i = 0; i < mLetters.length; i++) {
            //知道每个字母的中心位置    1 字母的高度一半    2 字母高度一般+前面字符的高度
            int letterCenterY = i * itemHeight + itemHeight / 2 + getPaddingTop();
            //基线：基于中心位置，知道中心位置就直接知道基线了(中心位置不是基线)
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int dy = (int) ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
            int baseLine = letterCenterY + dy;
            // x绘制在最中间 = 宽度/2 - 文字/2
            int textWidth = (int) mPaint.measureText(String.valueOf(mLetters[i]));
            int x = getWidth() / 2 - textWidth / 2;

            //如果当前字母要高亮(这里设置两个Paint比较好一点，因为如果一直改颜色的话，会一直去调用底层C代码，影响性能)
            if (mLetters[i] == mCurTouchLetter) {
                canvas.drawText(String.valueOf(mLetters[i]), x, baseLine, mSelectedPaint);
            } else {
                canvas.drawText(String.valueOf(mLetters[i]), x, baseLine, mPaint);
            }
        }
    }

    /**
     * 优化：
     * TODO:目前我暂时还不知道怎么去做
     * 每次都会来调用onDraw方法，先判断这个是否存在了，如果已经存在了，就不需要用invalidate()重绘
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //计算出当前触摸字母 ： 获取当前位置(这里可能会有负数，要处理一下)
                float curMoveY = event.getY();
                //位置 = curMoveY / 字母高度，通过位置获取字母
                int itemHeight = (getHeight() - getPaddingTop() - getPaddingBottom()) / mLetters.length;
                int curPosition = (int) (curMoveY / itemHeight);

                if (curPosition < 0) {
                    curPosition = 0;
                } else if (curPosition > mLetters.length - 1) {
                    curPosition = mLetters.length - 1;
                }

                mCurTouchLetter = mLetters[curPosition];
                mLetterTouchListener.touch(String.valueOf(mCurTouchLetter));
                //重新绘制
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mLetterTouchListener.up();
                break;
        }
        return true;
    }

    private float sp2Px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, getResources().getDisplayMetrics());
    }

    public void setLetterTouchListener(LetterTouchListener listener){
        mLetterTouchListener = listener;
    }

    //其他View不会使用这个接口，所以这个只需要写在我们这个View的里面，如果两个以上的View要使用这个接口，就把这个接口写到外面去
    public interface LetterTouchListener{
        void touch(String letter);
        void up();
    }
}
