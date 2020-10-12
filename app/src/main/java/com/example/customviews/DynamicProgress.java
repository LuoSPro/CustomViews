package com.example.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 分析效果：
 * 大圆、小圆、文字、动画
 *
 * 确定自定义属性
 */
public class DynamicProgress extends View {
    //自定义属性
    private int mRoundColor;
    private int mProgressColor;
    private int mProgressTextColor;
    private float mRoundWidth;
    private float mProgressTextSize;
    private int mProgress = 20;

    //绘制准备
    private Paint mPaint;

    public DynamicProgress(Context context) {
        this(context,null);
    }

    public DynamicProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DynamicProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.DynamicProgress);
        mProgressColor = typedArray.getColor(R.styleable.DynamicProgress_ProgressColor,mProgressColor);
        mRoundColor = typedArray.getColor(R.styleable.DynamicProgress_RoundColor,mRoundColor);
        mProgressTextColor = typedArray.getColor(R.styleable.DynamicProgress_ProgressTextColor,mProgressTextColor);
        mRoundWidth = typedArray.getDimensionPixelSize(R.styleable.DynamicProgress_RoundWidth, (int) mRoundWidth);
        mProgressTextSize = typedArray.getDimensionPixelSize(R.styleable.DynamicProgress_ProgressTextSize, (int) mProgressTextSize);
        typedArray.recycle();
    }

    private void init() {
        mProgressColor = Color.GREEN;
        mRoundColor = Color.RED;
        mProgressTextColor = Color.BLUE;
        mRoundWidth = 10;
        mProgressTextSize = 20;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int min = Math.min(width,height);
        setMeasuredDimension(min,min);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //获取View属性
        int width = getWidth();
        int height = getHeight();

        //绘制准备
        mPaint = new Paint();
        //转角
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);

        float centerX = (float) (1.0*width/2);
        float centerY = (float) (1.0*height/2);
        float radius = (float) (1.0*width/3);


        //画外圆
        mPaint.setColor(mRoundColor);
        RectF oval = new RectF(centerX-radius,centerY-radius,centerX+radius,centerY+radius);
        canvas.drawArc(oval,0,360,false,mPaint);

        //画内圆
        float percent = (float) (1.0*mProgress/100);
        mPaint.setColor(mProgressColor);
        canvas.drawArc(oval,0,percent*360,false,mPaint);

        //画文字前准备
        mPaint.reset();
        mPaint.setColor(mProgressTextColor);
        mPaint.setTextSize(mProgressTextSize);
        mPaint.setAntiAlias(true);

        //画文字
        String text = mProgress + "%";
        Rect textBound = new Rect();
        mPaint.getTextBounds(text,0,text.length(),textBound);
        int dx = (width-textBound.width())/2;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //计算文字基线
        int baseLine = (int)(getHeight() / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
        canvas.drawText(text,dx,baseLine,mPaint);
    }

    public void setProgress(int progress){
        mProgress = progress;
        invalidate();
    }
}
