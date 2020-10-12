package com.example.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class QQExerciseSteps extends View {
    //View的宽高
    private int mViewWidth;
    private int mViewHeight;
    //画笔
    private Paint mPaint;
    //画笔的宽度
    private float mRoundWidth;
    //画笔的颜色
    private int mRoundColor;
    //开始的角度
    private float mStartAngle;
    //结束的角度
    private float mSweepAngle;
    private int mProgressColor;
    private int mProgressStep;
    private int mMaxStep;
    private float mTextSize;
    private int mTextColor;

    public QQExerciseSteps(Context context) {
        this(context,null);
    }

    public QQExerciseSteps(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QQExerciseSteps(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQExerciseSteps);
        mTextColor = typedArray.getColor(R.styleable.QQExerciseSteps_stepTextColor, mTextColor);
        mRoundColor = typedArray.getColor(R.styleable.QQExerciseSteps_outerColor,mRoundColor);
        mProgressColor = typedArray.getColor(R.styleable.QQExerciseSteps_innerColor,mProgressColor);
        mRoundWidth = typedArray.getDimensionPixelSize(R.styleable.QQExerciseSteps_borderWidth, (int) mRoundWidth);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.QQExerciseSteps_stepTextSize, (int) mTextSize);
        //回收资源
        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mRoundWidth = 12f;
        mRoundColor = R.color.colorPrimary;
        mProgressColor = R.color.colorAccent;
        mStartAngle = 135f;
        mSweepAngle = 270f;
        mProgressStep = 150;
        mMaxStep = 200;
        mTextSize = 14;
        mTextColor = R.color.colorAccent;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //调用者在布局文件中可能是wrap_content，或宽度高度不一致
        //获取模式 AT_MOST  40dp

        //宽度高度不一致，取最小值，确保是个正方形
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int min = Math.min(width, height);
        setMeasuredDimension(min,min);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //这一步不能在初始化的时候做，必须得在omMeasure后面做，不然一开始得不到宽高数据
        mViewHeight = getWidth();
        mViewWidth = getWidth();
        //画背景圆弧
        //设置画笔的宽度
        mPaint.setStrokeWidth(mRoundWidth);
        //为画笔设置ROUND
        //设置画笔的线冒样式:ROUND： 半圆形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //段之间连接处的样式:.ROUND：圆弧
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //设置画笔颜色
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        //画大圆弧
        int centerX = mViewWidth/2;
        int centerY = mViewHeight/2;
        //半径
        int radius = (int) ((centerX - mRoundWidth)/2);
        @SuppressLint("DrawAllocation")
        RectF oval = new RectF(centerX-radius, centerY-radius, centerX+radius, centerY+radius);
        //画背景圆弧
        canvas.drawArc(oval,mStartAngle,mSweepAngle,false,mPaint);

        //画当前进度圆弧
        mPaint.setColor(mProgressColor);
        //计算当前百分比
        float percent = (float) (1.0*mProgressStep/mMaxStep);
        //根据当前百分比计算圆弧扫描的角度
        canvas.drawArc(oval,mStartAngle,percent*mSweepAngle,false,mPaint);

        //画步数、文字
        //重置mPaint的属性
        mPaint.reset();
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        String mStep = mProgressStep + "";
        //测量文字的宽高
        Rect textBounds = new Rect();
        mPaint.getTextBounds(mStep,0,mStep.length(),textBounds);
        //获取画笔的FontMetrics
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        int dx = (getWidth() - textBounds.width()) / 2;
        //计算文字基线
        int baseLine = (int)(getHeight() / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
        //绘制步数文字
        canvas.drawText(mStep,dx,baseLine,mPaint);
    }

    public void setStepMax(int stepMax){
        this.mMaxStep = stepMax;
    }

    public void setCurStep(int curStep){
        this.mProgressStep = curStep;
        invalidate();
    }
}
