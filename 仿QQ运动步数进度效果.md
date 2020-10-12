自定义View

## 一、仿QQ运动步数进度效果

### 1、分析：

1. 固定蓝色大圆弧：color、borderWidth
2. 可变的红色小圆弧：color、borderWidth
3. 中间的步数文字：color、textSize

还得为其设置自定义属性，使自定义View更灵活

### 2、步骤

1. 分析效果
2. 确定自定义属性，编写attrs.xml
3. 在布局中使用
4. 在自定义View中获取自定义属性
5. onMeasure()
6. 画外圆弧、内圆弧、文字
7. 其他

### 3、具体步骤

#### 1）分析效果

大圆弧、小圆弧、文字、动画

#### 2）自定义属性

```xml
<declare-styleable name="QQExerciseSteps">
    <attr name="outerColor" format="color"/>
    <attr name="innerColor" format="color"/>
    <attr name="borderWidth" format="dimension"/>
    <attr name="stepTextSize" format="dimension"/>
    <attr name="stepTextColor" format="color"/>
</declare-styleable>
```

#### 3）布局中使用

```xml
<com.example.qqexercisesteps.QQExerciseSteps
    android:id="@+id/step_view"
    app:outerColor="@color/colorPrimary"
    app:innerColor="@color/colorAccent"
    app:borderWidth="6dp"
    app:stepTextSize="26sp"
    app:stepTextColor="@color/colorPrimary"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

#### 4）自定义View中获取自定义属性

```java
TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQExerciseSteps);
mTextColor = typedArray.getColor(R.styleable.QQExerciseSteps_stepTextColor, mTextColor);
mRoundColor = typedArray.getColor(R.styleable.QQExerciseSteps_outerColor,mRoundColor);
mProgressColor = typedArray.getColor(R.styleable.QQExerciseSteps_innerColor,mProgressColor);
mRoundWidth = typedArray.getDimensionPixelSize(R.styleable.QQExerciseSteps_borderWidth, (int) mRoundWidth);
mTextSize = typedArray.getDimensionPixelSize(R.styleable.QQExerciseSteps_stepTextSize, (int) mTextSize);
//回收资源
typedArray.recycle();
```

`context.obtainStyledAttributes(attrs, R.styleable.QQExerciseSteps);`是通过context对象去获得一个TypeArray对象，我们自定义的属性全在这个对象中可以找到

每次使用TypeArray对象后，记得调用recycle()方法，把资源释放掉

#### 5）onMeasure

```java
int width = MeasureSpec.getSize(widthMeasureSpec);
int height = MeasureSpec.getSize(heightMeasureSpec);
int min = Math.min(width, height);
setMeasuredDimension(min,min);
```

onMeasure()方法是对我们自定义View进行测量，如果我们想得到自定义View的宽或高，必须要先在onMeasure中去测量之后（即通过`setMeasuredDimension`设置View的大小），然后在onLayout或onDraw中才能通过getHeight()方法去得到自定义View的高度。

#### 6）画外圆弧、内圆弧、文字

前面的准备工作做好之后，我们开始在onDraw方法中去绘制我们的View。

在绘制之前，我们得准备好两个东西：

- Paint对象
- View得宽高

View的宽高我们之前提到了，就是在onMeasure方法后，直接通过getWidth()和getHeight()就可以直接获得了。

```java
//这一步不能在初始化的时候做，必须得在omMeasure后面做，不然一开始得不到宽高数据
mViewHeight = getWidth();
mViewWidth = getWidth();
```

而Paint对象，我们还是需要对他进行一些初始化处理

```java
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
```

绘制外圆弧：

绘制外圆弧时，主要是通过调用canvas对象的drawArc方法，他里面的参数

```java
public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter,
            @NonNull Paint paint)
```

- RectF：是一个矩阵对象，它用于定义圆弧的大小和形状
- startAngle：圆弧开始角度（以x轴正方向，顺时针计算角度）
- sweepAngle：掠过的角度
- useCenter：false
- paint：画笔

而RectF这个矩阵对象是用于定义圆弧的，我们需要一个圆时，以其中心点开始，半径扫过的面积就是他的区域

```java
RectF oval = new RectF(centerX-radius, centerY-radius, centerX+radius, centerY+radius);
```



绘制内圆弧：

由于外圆弧和内圆弧都是在一段弧上面，除了画笔颜色和圆弧掠过的角度需要改一下之外，其他都类似

```java
 //画当前进度圆弧
 mPaint.setColor(mProgressColor);
 //计算当前百分比
 float percent = (float) (1.0*mProgressStep/mMaxStep);
 //根据当前百分比计算圆弧扫描的角度
 canvas.drawArc(oval,mStartAngle,percent*mSweepAngle,false,mPaint);
```



绘制文字：

先在这里看一下绘制文字的区域

![img](https://upload-images.jianshu.io/upload_images/4314397-f1228aa14757d88c?imageMogr2/auto-orient/strip|imageView2/2/format/webp)

和前面两个不一样的是，这里绘制文字时的Paint对象已经不一样了，所以我们需要重新设置Paint对象的属性

```java
//重置mPaint的属性
mPaint.reset();
//设置抗锯齿
mPaint.setAntiAlias(true);
mPaint.setTextSize(mTextSize);
mPaint.setColor(mTextColor);
```

绘制文字主要用的是canvas的drawText()属性，我们可以看一下参数

```java
public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint)
```

- text：我们要绘制的内容
- x：文本原点的x坐标
- y：文本基线的y坐标
- paint：画笔

对于文本原点位置，我们是要文本在整个View的中间，所以是: (view的宽 - 文字区域的长度) / 2

```java
int dx = (getWidth() - textBounds.width()) / 2;
```

而对于文本的基线，我们需要

```java
//获取画笔的FontMetrics
Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
//计算文字基线
int baseLine = (int)(getHeight() / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
```

在Paint默认的属性中，并没有FontMetrics的数据，我们需要给mPaint设置我们要绘制的文本的区域来获得，所在，在得到FontMetrics对象前，我们需要先给Paint设置文本的区域

```java
//测量文字的宽高
Rect textBounds = new Rect();
mPaint.getTextBounds(mStep,0,mStep.length(),textBounds);
```

最后

```java
//绘制步数文字
canvas.drawText(mStep,dx,baseLine,mPaint);
```

#### 7）其他

结果上面的步骤，我们基本能看到View的效果了，不过，这个时候的View还是静态的，我们要让他动起来，就得给View添加动画

```kotlin
val valueAnimator = ObjectAnimator.ofFloat(0f,3000f)
valueAnimator.apply {
    duration = 1000
    interpolator = DecelerateInterpolator()
    addUpdateListener (object : ValueAnimator.AnimatorUpdateListener{
        override fun onAnimationUpdate(animation: ValueAnimator?) {
            //这里只能先转成Float，直接先转成Int会崩掉
            val curStep: Float = animation?.animatedValue as Float
            step_view.setCurStep(curStep.toInt())
        }
    })
    start()
}
```

除了动画，我们还可以添加一些方法，使得View更灵活，或者设置一些监听，帮助我们控制View的状态



