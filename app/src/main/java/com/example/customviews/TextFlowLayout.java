package com.example.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程：
 * 1.自定义属性
 * 2.初始化
 * 3.measure
 *   3.1 调用super对自己进行测量
 *   3.2 获取自身的width，以便后面对宽度进行判断，用于换行
 *   3.3 用for循环遍历子View，对子View的数据进行判断，并保存下来，以便layout的时候根据数据对子View进行摆放
 * 4.layout
 */
public class TextFlowLayout extends ViewGroup {

    private static final String TAG = "TextFlowLayout";
    
    private int mTextLines;
    private int mTextColor;
    private float mTextSize;
    private Context mContext;
    private List<String> mTextList;
    private List<TextView> mOneLine;
    private List<List<TextView>> mLines;
    
    public TextFlowLayout(Context context) {
        this(context,null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.TextFlowLayout);
        mTextLines = (int) ta.getDimension(R.styleable.TextFlowLayout_TextLines, mTextLines);
        mTextSize = (int) ta.getDimension(R.styleable.TextFlowLayout_TextLines, mTextSize);
        mTextColor = ta.getColor(R.styleable.TextFlowLayout_TextColor,mTextColor);
        ta.recycle();
    }

    private void init(Context context) {
        mContext = context;
        mTextColor = Color.RED;
        mTextLines = 2;
        mTextSize = 16;
        mOneLine = new ArrayList<>();
        mLines = new ArrayList<>();
    }
    
    public void setTextList(List<String> textList){
        this.mTextList.addAll(textList);
        //先将List清空，不然会一直保留之前的数据
        mLines.clear();
        mOneLine.clear();
        //将每个View添加到ViewGroup中
        for (int i = 0; i < textList.size(); i++) {
            //这里就直接用代码创建子View
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            textView.setText(textList.get(i));
            addView(textView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里已经对ViewGroup进行了测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //清空集合
        mLines.clear();
        mOneLine = new ArrayList<>();
        mLines.add(mOneLine);

        int childCount = getChildCount();

        //获取自身宽度
        //由于前面调用了super.onMeasure，所以这里已经有数据了
        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        //这里获取到的height是一行的height，我们因为要涉及到换行，所以得改变最后的height
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

        //为什么这里不用mTextList.size
        Log.d(TAG, "onMeasure: childCount ==> " + childCount);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //虽然前面能得到ViewGroup的宽高，但是这里的每个子View的宽高是不知道的
            //所以应该对每个子View进行测量
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            //因为对子View进行了测量，所以现在可以拿子View的属性了
            int childWidth = child.getPaddingStart() + child.getPaddingEnd() + child.getWidth();
            //除了padding和width，还有margin没考虑
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            //leftMargin 和 rightMargin相较于这的区别
            int width = childWidth + params.getMarginStart() + params.getMarginEnd();

            Log.d(TAG, "onMeasure: child width ==> " + width);
            //如果mOneLine已经放不下了，那么就新创建一行
            if (childWidth > selfWidth - width*(mOneLine.size())){
                mOneLine = new ArrayList<>();
                mLines.add(mOneLine);

                //高度改变
                selfHeight += selfHeight;
            }
            mOneLine.add((TextView) child);
            //宽度改变
            selfWidth += width;
            Log.d(TAG, "onMeasure: selfWidth ==> " + selfWidth);
        }
        Log.d(TAG, "onMeasure: selfWidth ==> " + selfWidth + " selfHeight ==> " + selfHeight);
        setMeasuredDimension(selfWidth,selfHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left;
        int right;
        int top = getPaddingTop();
        int bottom;
        //由于在measure的时候，已经把子View的摆放位置基本确定好了，这里直接摆放就行
        for (List<TextView> line : mLines) {
            left = getPaddingLeft();

            for (TextView view : line) {
                MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
                left += params.leftMargin;
                int childTop = top + params.topMargin;
                right = left + view.getMeasuredWidth();
                bottom = top + view.getMeasuredHeight();
                view.layout(left,childTop,right,bottom);
                left = right + params.rightMargin;
            }
            top += getChildAt(0).getHeight();
        }
    }
}
