### 一、自定义View的套路：

#### 1. 自定义属性，获取自定义属性（达到配置的效果）

#### 2. onMeasure()方法用于测量自己的宽高

前提是继承自View，如果是继承自系统已有的View(TextView、Button)，它已经实现好了onMeasure方法去计算宽高

#### 3. onDraw()用于绘制自己的显示

#### 4. onTouch()用于与用户交互

### 二、 自定义ViewGroup的套路

#### 1. 自定义属性，

获取自定义属性(达到配置的效果)，不常用

#### 2. onMeasure()方法，

for循环测量子View，根据子View的宽高来计算自己的宽高

#### 3. onDraw()方法

一般不需要，默认情况下是不会调用的，如果你要绘制需要实现dispatchDraw()方法

#### 4. onLayout()用来摆放子View

前提是，子View不是GONE的情况

#### 5. 注意 

在很多情况下，不会继承自ViewGroup，往往是继承自系统已经提供好的ViewGroup，如ViewPager、RelativeLayout，这种系统做好的ViewGroup，我们不需要去实现onLayout()等方法，重点的是onTouch()方法

### 三、View的onTouch事件分发 

#### 1. 现象：

##### 现象一

OnTouchListener、OnTouch、OnClickListener三种方法都监听的时候:（onTouchListener 返回false时）

```
D/MainActivity: setOnTouchListener ==> 0
D/TouchView: onTouchEvent: event.getAction() ==> 0
D/MainActivity: setOnTouchListener ==> 1
D/TouchView: onTouchEvent: event.getAction() ==> 1
D/MainActivity: onClick ==> 
```

OnTouchListener.DOWN ==> OnTouch.DOWN ==> OnTouchListener.MOVE ==> OnTouch.MOVE ==>  OnTouchListener.UP ==> OnTouch.UP ==> OnClickListener

##### 现象二

OnTouchListener、OnTouch、OnClickListener三种方法都监听的时候:（onTouchListener 返回true时）

```
D/MainActivity: setOnTouchListener ==> 0
D/MainActivity: setOnTouchListener ==> 1
```

OnTouchListener.DOWN ==> OnTouchListener.MOVE  ==>  OnTouchListener.UP 

##### 现象三

重写View的OnTouchEvent()方法，并将返回值直接写成return true

```java
public boolean onTouchEvent(MotionEvent event) {
    Log.d(TAG, "onTouchEvent: event.getAction() ==> " + event.getAction());
    return true;
}
```

那么最后，onClickListener不会被执行

```
D/MainActivity: setOnTouchListener ==> 0
D/TouchView: onTouchEvent: event.getAction() ==> 0
D/MainActivity: setOnTouchListener ==> 1
D/TouchView: onTouchEvent: event.getAction() ==> 1
```

OnTouchListener.DOWN ==> OnTouch.DOWN ==> OnTouchListener.MOVE ==> OnTouch.MOVE ==>  OnTouchListener.UP ==> OnTouch.UP 

##### 现象四

```java
public boolean dispatchTouchEvent(MotionEvent event) {
    //如果没有这句话，那么onTouchListener、onTouchEvent、onClickListener这三个都不会被调用，
    super.dispatchTouchEvent(event);
    return true;
}
```

#### 2. View与Touch相关的有两个非常重要的方法

##### 1）disoatchTouchEvent()：做事件分发

我们来看View.java里面的这个方法

```java
public boolean dispatchTouchEvent(MotionEvent event) {
    
    boolean result = false;
    if (onFilterTouchEventForSecurity(event)) {
        //noinspection SimplifiableIfStatement
        //把所有的监听事件封装成了一个对象，这里面存放了View所有Listener信息，如onTouchListener
        ListenerInfo li = mListenerInfo;
        if (li != null && li.mOnTouchListener != null
                && (mViewFlags & ENABLED_MASK) == ENABLED  //是否是enable
                && li.mOnTouchListener.onTouch(this, event)) {  //如果你mOnTouchListener返回的是false，那么result就为false
            result = true;
        }

        if (!result && onTouchEvent(event)) {//如果result = false，就会执行onTouchEvent，如果result = true，就不会执行就会执行onTouchEvent
            result = true;
        }
    }
    //返回
    return result;
}

boolean isAccessibilityFocusedViewOrHost() {
    return isAccessibilityFocused() || (getViewRootImpl() != null && getViewRootImpl()
            .getAccessibilityFocusedHost() == this);
}

static class ListenerInfo {
    protected OnFocusChangeListener mOnFocusChangeListener;
    private ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
    protected OnScrollChangeListener mOnScrollChangeListener;
    private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;
    public OnClickListener mOnClickListener;
}
```

这个方法里面，其实就是利用 ==短路与== 的特性：当前面的条件不符合时，就不再判断后面的条件了，所以就通过这种方式，让enable属性控制mOnTouchListener方法的执行，和让result变量控制onTouchEvent的执行

##### 2）onTouchEvent()：一般都会被我们复写

我们看源码：

```java
public boolean onTouchEvent(MotionEvent event) {
    final float x = event.getX();
    final float y = event.getY();
    final int viewFlags = mViewFlags;
    final int action = event.getAction();
    
    if (clickable || (viewFlags & TOOLTIP) == TOOLTIP) {
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (mPerformClick == null) {
                    mPerformClick = new PerformClick();
                }
                if (!post(mPerformClick)) {
                    performClickInternal();
                }
        }
    }
}

private boolean performClickInternal() {
    // Must notify autofill manager before performing the click actions to avoid scenarios where
    // the app has a click listener that changes the state of views the autofill service might
    // be interested on.
    notifyAutofillManagerOnClick();

    return performClick();
}

public boolean performClick() {
    if (li != null && li.mOnClickListener != null) {
        playSoundEffect(SoundEffectConstants.CLICK);
        //调用点击事件
        li.mOnClickListener.onClick(this);
        result = true;
    } else {
        result = false;
    }
    return result;
}
```

这里其实就解释了，为什么我们OnTouchListener里面返回false的时候，因为View的onClickListener是在OnTouch.UP后面才调用的

##### 6)onTouchEvent()复写返回 return  true

当onTouchEvent返回true后，这个方法就没有去调用super.onTouchEvent()方法，View内部的onTouchEvent()方法就不能得到执行，就不能去调用performClick()方法，那么`li.mOnClickListener.onClick(this);`就不能执行。所以onClickListener就不能得到执行

 