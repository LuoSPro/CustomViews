package com.example.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask.Status;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class LoadingView58 extends View {
    private Paint mPaint;
    private int mTriangleColor;
    private int mRectangleColor;
    private int mCircleColor;

    private Status curStatus = null;
    private Path mPath;

    public LoadingView58(Context context) {
        this(context,null);
    }

    public LoadingView58(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView58(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.LoadingView58);
        mTriangleColor = typedArray.getColor(R.styleable.LoadingView58_TriangleColor,mTriangleColor);
        mRectangleColor = typedArray.getColor(R.styleable.LoadingView58_TriangleColor,mRectangleColor);
        mCircleColor = typedArray.getColor(R.styleable.LoadingView58_CircleColor,mCircleColor);

        typedArray.recycle();
    }

    private void init(){
        mCircleColor = Color.BLUE;
        mTriangleColor = Color.GREEN;
        mRectangleColor = Color.RED;
        mPaint = new Paint();
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int min = Math.min(width, height);
        setMeasuredDimension(min,min);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(20f);
        mPaint.setAntiAlias(true);

        float centerX = (float) (1.0*width/2);
        float centerY = (float) (1.0*height/2);
        float radius = (float) (1.0*width/8);
        float side = (float) (1.0*width/8);
        //画圆
        if (curStatus == Status.CIRCLE){
            mPaint.setColor(mCircleColor);
            canvas.drawCircle(centerX,centerY,radius,mPaint);
        }else if(curStatus == Status.RECTANGLE){
            mPaint.setColor(mRectangleColor);
            mPath.moveTo(centerX-side,centerY-side);
            mPath.lineTo(centerX-side,centerY+side);
            mPath.lineTo(centerX+side,centerY+side);
            mPath.lineTo(centerX+side,centerY-side);
            mPath.lineTo(centerX-side,centerY-side);
            canvas.drawPath(mPath,mPaint);
//            canvas.drawRect(centerX-side,centerY-side,centerX+side,centerY-side,mPaint);
        }else if(curStatus == Status.TRIANGLE){
            mPaint.setColor(mTriangleColor);
            mPath.moveTo(centerX-radius,centerY);
            mPath.lineTo(centerX+radius,centerY);
            mPath.lineTo(centerX,centerY-radius);
            mPath.lineTo(centerX-radius,centerY);
            canvas.drawPath(mPath,mPaint);
        }

        //画矩形


        //画三角形
    }

    enum Status{
        TRIANGLE,RECTANGLE,CIRCLE
    }

    public void setStatus(Status status){
        curStatus = status;
        invalidate();
    }
}
