package com.example.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_content.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * 方法一：
         * 在内容页加一个半透明的View，利用onScrollChanged回调scale值给Activity，在Activity中给阴影设置alpha效果可以实现，
         * 但是站在开发的角度不行
         */
        //(能实现功能，但在架构方面不行)
        //这样的效果不好，因为你是要给别人用，别人使用的时候还得来配置，但是效果是可以做得到的
//        sliding_menu.setScaleChangeListener {
//            shadow_view.alpha = it;
//        }

        /**
         * 方法二：
         * 阴影想办法放到自定义View里面，把内容布局单独提取出来，然后在外面套一层阴影，最后再把容器放回原来的位置(BadgeView)
         */

    }
}
