package com.example.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class LockPatternView : View {

    private var mIsErrorStatus: Boolean = false
    private var mIsSuccessStatus: Boolean = false

    //是否初始化，确保只初始化一次
    private var mIsInit = false

    //外圆的半径
    private var mDotRadius: Float = 0f

    //二维数组，3x3
    private var mPoints: Array<Array<Point?>> = Array(3) { Array<Point?>(3) { null } }

    // 画笔
    private lateinit var mLinePaint: Paint
    private lateinit var mPressedPaint: Paint
    private lateinit var mErrorPaint: Paint
    private lateinit var mNormalPaint: Paint
    private lateinit var mArrowPaint: Paint

    //颜色(内圆、外圆)
    private val mOuterPressedColor = 0xff8cbad8.toInt()
    private val mInnerPressedColor = 0xff0596f6.toInt()
    private val mOuterNormalColor = 0xffd9d9d9.toInt()
    private val mInnerNormalColor = 0xff929292.toInt()
    private val mOuterErrorColor = 0xff901032.toInt()
    private val mInnerErrorColor = 0xffea0945.toInt()

    /**
     *  按下的时候，是否选中了点
     */
    private var mIsTouchPoint = false

    /**
     * 选中的所有点
     */
    private var mSelectPoints = ArrayList<Point>()


    //构造函数
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        width = if (width>height) height else width
        setMeasuredDimension(width,width)
    }

    override fun onDraw(canvas: Canvas?) {
        //初始化9个宫格，onDraw()会调用多次
        if (!mIsInit) {
            initDot()
            initPaint()
            mIsInit = true
        }
        //刷新
        invalidate()
        //绘制9个宫格，根据三种不同的状态，分别进行绘制
        drawShow(canvas!!)
    }

    /**
     * 初始化绘制：绘制9宫格的显示
     */
    private fun drawShow(canvas: Canvas) {

        for (i in 0..2) {
            for (point in mPoints[i]) {
                if (point!!.status == point.STATUS_NORMAL) {
                    //先绘制外圆
                    mNormalPaint.color = mOuterNormalColor
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius, mNormalPaint)
                    //后绘制内圆（内圆是外圆1/6的大小）
                    mNormalPaint.color = mInnerNormalColor
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius / 6, mNormalPaint)
                }

                if (point.status == point.STATUS_PRESSED) {
                    //先绘制外圆
                    mNormalPaint.color = mOuterPressedColor
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius, mNormalPaint)
                    //后绘制内圆（内圆是外圆1/6的大小）
                    mNormalPaint.color = mInnerPressedColor
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius / 6, mNormalPaint)
                }

                if (point.status == point.STATUS_ERROR) {
                    //先绘制外圆
                    mNormalPaint.color = mOuterErrorColor
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius, mNormalPaint)
                    //后绘制内圆（内圆是外圆1/6的大小）
                    mNormalPaint.color = mInnerErrorColor
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius / 6, mNormalPaint)
                }
            }
        }
        //绘制两个点之间的连线以及箭头
        drawLines(canvas);
    }

    /**
     * 绘制两个点之间的连线以及箭头
     */
    private fun drawLines(canvas: Canvas) {
        var lastPoint: Point? = null
        if (mSelectPoints.size >= 1) {
            //绘制前，先确定画笔的颜色
            if (mIsErrorStatus) {
                mLinePaint.color = mInnerErrorColor
                mArrowPaint.color = mInnerErrorColor
            }else{
                mLinePaint.color = mInnerPressedColor
                mArrowPaint.color = mInnerPressedColor
            }

            lastPoint = mSelectPoints[0]
            //两个点之间需要绘制一条线和箭头
            for (point in mSelectPoints) {
                //两个点之间绘制一条线
                drawLine(lastPoint!!, point, canvas, mLinePaint)
                //两个点之间绘制一个箭头
                drawArrow(canvas,mArrowPaint,lastPoint,point,(mDotRadius / 4),38)
                //更新上一个Point
                lastPoint = point
            }
        }

        lastPoint?.let {
            //如果手指在内圆里面就不要绘制了
            //当前点和手指目前所在的位置
            var isInnerPoint = MathUtil.checkInRound(lastPoint.centerX,lastPoint.centerY,mDotRadius/4,mMovingX,mMovingY)
            //绘制最后一个点到手指当前位置的连线
            if (!isInnerPoint && mIsTouchPoint){
                drawLine(lastPoint, Point(mMovingX,mMovingY,-1),canvas,mLinePaint)
            }
        }
    }

    /**
     * 绘制点与点之间的线
     */
    private fun drawLine(start: Point,end: Point,canvas: Canvas,paint: Paint) {
        val pointDistance = MathUtil.distance(start.centerX.toDouble(),start.centerY.toDouble(),end.centerX.toDouble(),end.centerY.toDouble())

        var dx = end.centerX - start.centerX
        var dy = end.centerY - start.centerY

        //dx / pointDistance : cos
        //(mDotRadius / 6.0) : 斜边
        //rx = 斜边 * cos
        val rx = (dx / pointDistance * (mDotRadius / 6.0)).toFloat()
        val ry = (dy / pointDistance * (mDotRadius / 6.0)).toFloat()

        canvas.drawLine(start.centerX + rx ,start.centerY + ry,end.centerX - rx,end.centerY - ry,paint)
    }

    /**
     * 画箭头
     */
    private fun drawArrow(canvas: Canvas, paint: Paint, start: Point, end: Point, arrowHeight: Float, angle: Int) {
        val d = MathUtil.distance(start.centerX.toDouble(), start.centerY.toDouble(), end.centerX.toDouble(), end.centerY.toDouble())
        val sin_B = ((end.centerX - start.centerX) / d).toFloat()
        val cos_B = ((end.centerY - start.centerY) / d).toFloat()
        val tan_A = Math.tan(Math.toRadians(angle.toDouble())).toFloat()
        val h = (d - arrowHeight.toDouble() - mDotRadius * 1.1).toFloat()
        val l = arrowHeight * tan_A
        val a = l * sin_B
        val b = l * cos_B
        val x0 = h * sin_B
        val y0 = h * cos_B
        val x1 = start.centerX + (h + arrowHeight) * sin_B
        val y1 = start.centerY + (h + arrowHeight) * cos_B
        val x2 = start.centerX + x0 - b
        val y2 = start.centerY.toFloat() + y0 + a
        val x3 = start.centerX.toFloat() + x0 + b
        val y3 = start.centerY + y0 - a
        //画一个路径
        val path = Path()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.close()
        canvas.drawPath(path, paint)
    }


    /**
     * 初始化画笔
     * 3个点状态的画笔：格子，连线，箭头的画笔
     * 实例化+设置颜色
     */
    private fun initPaint() {
        // 线的画笔
        mLinePaint = Paint()
        mLinePaint.apply {
            color = mInnerPressedColor
            style = Paint.Style.STROKE
            //防锯齿
            isAntiAlias = true
            strokeWidth = mDotRadius / 9
        }
        // 按下的画笔
        mPressedPaint = Paint()
        mPressedPaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = mDotRadius / 6
        }
        // 错误的画笔
        mErrorPaint = Paint()
        mErrorPaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = mDotRadius / 6
        }
        // 默认的画笔
        mNormalPaint = Paint()
        mNormalPaint.apply {
            color = mOuterNormalColor
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = mDotRadius / 9
        }
        // 箭头的画笔
        mArrowPaint = Paint()
        mArrowPaint.apply {
            color = mInnerPressedColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    /**
     * 初始化点
     */
    private fun initDot() {
        //9个宫格,存到集合 Point[3][3]
        //不断绘制的时候这几个点都有状态，而且后面肯定需要回调密码，点都有下标，把点抽象成一个对象
        //计算中心位置
        var width = this.width
        var height = this.height

        //兼容横竖屏
        var offsetX = 0
        var offsetY = 0
        if (height > width) {
            //使最后的九宫格是一个正方形，所以应该把矩形变成正方形
            offsetY = (height - width) / 2
            height = width
        } else {
            offsetX = (width - height) / 2
            width = height
        }

        //正方形每个格子的宽
        var squareWidth = width / 3
        //外圆的大小，根据宽度求得
        mDotRadius = width / 12f

        mPoints[0][0] = Point(offsetX + squareWidth / 2f, offsetY + squareWidth / 2f, 0)
        mPoints[0][1] = Point(offsetX + squareWidth * 3 / 2f, offsetY + squareWidth / 2f, 1)
        mPoints[0][2] = Point(offsetX + squareWidth * 5 / 2f, offsetY + squareWidth / 2f, 2)
        mPoints[1][0] = Point(offsetX + squareWidth / 2f, offsetY + squareWidth * 3 / 2f, 3)
        mPoints[1][1] = Point(offsetX + squareWidth * 3 / 2f, offsetY + squareWidth * 3 / 2f, 4)
        mPoints[1][2] = Point(offsetX + squareWidth * 5 / 2f, offsetY + squareWidth * 3 / 2f, 5)
        mPoints[2][0] = Point(offsetX + squareWidth / 2f, offsetY + squareWidth * 5 / 2f, 6)
        mPoints[2][1] = Point(offsetX + squareWidth * 3 / 2f, offsetY + squareWidth * 5 / 2f, 7)
        mPoints[2][2] = Point(offsetX + squareWidth * 5 / 2f, offsetY + squareWidth * 5 / 2f, 8)
    }

    //手指触摸的位置
    private var mMovingX = 0f
    private var mMovingY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //当绘制的是错误页面时，此时的View还在错误界面持续1s，这个时候，设置View的Touch事件不响应
        if (mIsErrorStatus||mIsSuccessStatus){
            return true
        }
        //记录当前位置
        mMovingX = event.x
        mMovingY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 判断手指是不是按在一个宫格上面，post的get方法会去判断当前触摸的位置有没有选中某个点，如果没有，就返回null，
                var point = this.point
                point?.let {
                    //当选中的point不为null时，说明选中了某个点，设置标记
                    mIsTouchPoint = true
                    mSelectPoints.add(it)
                    //改变当前点的状态为点击
                    it.status = it.STATUS_PRESSED
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //按下的时候一定要在一个点上，不断触摸的时候不断去判断新的点
                var point = this.point
                //point可能为null
                point?.let {
                    if (mIsTouchPoint) {
                        //查看当前这个point是否已经添加至List中了，这样就避免了已经经过的点再次被连接的情况
                        if (!mSelectPoints.contains(point)) {
                            mSelectPoints.add(point)
                        }
                        point.status = point.STATUS_PRESSED
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                //回调密码监听
                mIsTouchPoint = false
                if (mSelectPoints.size == 1) {
                    // 清空选择
                    clearSelectPoints()
                } else if (mSelectPoints.size <= 3) {
                    // 太短显示错误
                    showSelectError()
                } else {
                    // 成功回调
                    if (mListener != null) {
                        lockCallBack()
                    }
                }
            }
        }

        return true
    }

    fun showSelectError() {
        //改变点的状态
        for (selectPoint in mSelectPoints) {
            selectPoint.status = selectPoint.STATUS_ERROR
        }
        //改变线的状态
        mIsErrorStatus = true

        postDelayed({
            clearSelectPoints()
            mIsErrorStatus = false
            invalidate()
        }, 500)
    }

    private fun clearSelectPoints() {
        for (point in mSelectPoints){
            point.status = point.STATUS_NORMAL
        }
        //因为绘制线条的时候，是根据mSelectPoint这个集合里面的点，去两个点之间绘制，而我们现在把List清空，那么绘制
        //的时候找不到点，就自然不会去绘制线，这样同时达到了清空线的效果
        mSelectPoints.clear()
        invalidate()
    }

    private val point: Point?
        get() {
            for (i in 0..2) {
                //循环9个点，
                for (point in mPoints[i]) {
                    // 如何判断一个点在圆里面 点到圆心的距离 < 半径
                    if (MathUtil.checkInRound(point!!.centerX,point.centerY,mDotRadius,mMovingX,mMovingY)) {
                        return point
                    }
                }
            }
            return null
        }


    /**
     * 回调
     */
    private fun lockCallBack() {
        var password = ""
        for (selectPoint in mSelectPoints) {
            password += selectPoint.index
        }
        mIsSuccessStatus = true
        mListener!!.lock(password)
        postDelayed({
            clearSelectPoints()
            mIsSuccessStatus = false
            invalidate()
        }, 500)
    }

    private var mListener: LockPatternListener? = null
    fun setLockPatternListener(listener: LockPatternListener) {
        this.mListener = listener
    }

    interface LockPatternListener {
        fun lock(password: String)
    }


    /**
     * 宫格的类
     */
    class Point(var centerX: Float, var centerY: Float, var index: Int) {
        val STATUS_NORMAL = 1
        val STATUS_PRESSED = 2
        val STATUS_ERROR = 3

        //当前点的状态  默认情况下为NORMAL
        var status = STATUS_NORMAL
    }

}