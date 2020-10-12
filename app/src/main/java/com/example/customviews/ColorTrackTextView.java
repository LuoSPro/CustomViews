package com.example.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class ColorTrackTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int mOriginColor = Color.RED;
    private int mChangeColor = Color.BLUE;

    //绘制不变字体的画笔
    private Paint mOriginPaint;
    //绘制变色字体的画笔
    private Paint mChangePaint;
    //当前进度
    private float mCurProgress = 0.0f;
    //实现不同朝向
    private Direction mDirection = Direction.LEFT_TO_RIGHT;


    public enum Direction{
        LEFT_TO_RIGHT,RIGHT_TO_LEFT
    }

    public ColorTrackTextView(Context context) {
        this(context,null);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.ColorTrackTextView);
        mOriginColor = typedArray.getColor(R.styleable.ColorTrackTextView_originColor,mOriginColor);
        mChangeColor = typedArray.getColor(R.styleable.ColorTrackTextView_changeColor,mChangeColor);
        typedArray.recycle();
        init();
    }

    private void init() {
        mChangePaint = getPaintByColor(mChangeColor);
        mOriginPaint = getPaintByColor(mOriginColor);
    }

    /**
     * 根据颜色获取Paint
     * @param color
     * @return
     */
    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        //设置颜色
        paint.setColor(color);
        //防锯齿
        paint.setAntiAlias(true);
        //防抖动
        paint.setDither(true);
        //设置字体大小
        //这里的getTextSize()是因为继承了TextView，直接从布局那边获取的
        paint.setTextSize(getTextSize());
        return paint;
    }

    /**
     * 利用clipRect的API可以裁剪，左边用一个画笔去画，右边用另一个画笔去画，不断的改变中间值
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //根据进度把中间值算出来
        int middle = (int) (mCurProgress*getWidth());
        if (mDirection == Direction.LEFT_TO_RIGHT){
            drawText(canvas,mOriginPaint,0,getWidth());
            drawText(canvas,mChangePaint,0,middle);
        }else{
            drawText(canvas,mOriginPaint,getWidth()-middle,getWidth());
            drawText(canvas,mChangePaint,0,getWidth()-middle);
        }
    }

    /**
     * 绘制Text
     * @param canvas
     * @param paint
     * @param start
     * @param end
     */
    private void drawText(Canvas canvas,Paint paint,int start, int end){
        //这个save需要放到裁剪之前，不然后面直接对整个canvas进行裁剪后，后面的changePaint就发挥不了作用了
        canvas.save();
        //绘制不变色的
        Rect rect = new Rect(start,0,end,getHeight());
        //裁剪区域
        canvas.clipRect(rect);
        //如果这里调用了super.onDraw()方法，那么他就会让其父类TextView把文字画出来，就达不到我们渐变文字的效果了
        //super.onDraw(canvas);
        //所以我们自己来画
        String text = getText().toString();
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        //获取字体的宽度
        int x = getWidth()/2 - bounds.width()/2;//View的一半 - 文字的一半
        //基线baseLine
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top)/2 - fontMetricsInt.bottom;
        int baseLine = getHeight()/2 + dy;
        canvas.drawText(text,x,baseLine,paint);//这么画还是只有一种颜色
        canvas.restore();
    }

    public void setDirection(Direction direction){
        this.mDirection = direction;
    }

    public void setCurProgress(float progress){
        this.mCurProgress = progress;
        invalidate();
    }

    public void setOriginColor(int color) {
        mOriginPaint.setColor(color);
    }

    public void setChangeColor(int color) {
        mChangePaint.setColor(color);
    }
}
