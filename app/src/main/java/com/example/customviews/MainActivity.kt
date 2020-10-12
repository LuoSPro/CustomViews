package com.example.customviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDynamicProgress()
    }

    private fun initDynamicProgress() {
        //属性动画
        val valueAnimator = ObjectAnimator.ofFloat(0f, 100f)
        valueAnimator.apply {
            duration = 3000
            interpolator = DecelerateInterpolator()
            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(animation: ValueAnimator?) {
                    //这里只能先转成Float，直接先转成Int会崩掉
                    val curStep: Float = animation?.animatedValue as Float
                    progress_view.setProgress(curStep.toInt())
                }
            })
            start()
        }
    }
}
