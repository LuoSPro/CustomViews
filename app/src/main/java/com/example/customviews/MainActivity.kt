package com.example.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private var mImageViewHeight by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //刚进来背景完全透明
        title_bar.background.alpha = 0

        image_view.post(object : Runnable{
            override fun run() {
                mImageViewHeight = image_view.measuredHeight
            }
        })

        //不断的监听滚动，判断当前滚动的位置跟头部的imageview比较计算背景透明度
        scroll_view.setScrollChangeListener(object : MyScrollView.ScrollChangeListener{
            override fun onScroll(l: Int, t: Int, oldl: Int, oldt: Int) {
                //获取图片的高度，根据当前滚动的位置，计算alpha值
                if (mImageViewHeight == 0){
                    //避免除0
                    return
                }
                //ImageView的高度要减去titleBar的高度
                var alpha = t / (mImageViewHeight-title_bar.measuredHeight).toFloat()
                if (alpha < 0){
                    alpha = 0f
                }
                if (alpha > 1){
                    alpha = 1F
                }
                title_bar.background.alpha = (alpha*255).toInt()
            }
        })


    }
}
