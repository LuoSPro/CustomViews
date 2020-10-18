package com.example.customviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.view.LayoutInflaterCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

//        changeSkin()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandle = Handler(Looper.getMainLooper())
//        initQQStepView()
//        initDynamicProgress()
//        initLoadingView()
//        initColorTrackText()
//        initLetterSideBar()
        Log.d("MainActivity", tv_letter.toString())
//        View.inflate(this,R.layout.activity_main,null)
//        LayoutInflater.from(this).inflate(R.layout.activity_main,null)
//        LayoutInflater.from(this).inflate(R.layout.activity_main,null,false)

    }

    private fun changeSkin() {
        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, object : LayoutInflater.Factory2 {
            override fun onCreateView(
                parent: View?,
                name: String,
                context: Context,
                attrs: AttributeSet
            ): View? {
                //拦截到View的创建
                if (name == "Button") {
                    val textView = TextView(this@MainActivity)
                    textView.text = "被拦截了"
                    return textView
                }
                return null
            }

            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
                //拦截到View的创建
                if (name == "Button") {
                    val textView = TextView(this@MainActivity)
                    textView.text = "被拦截了"
                    return textView
                }
                return null
            }
        })
    }

    private fun initLetterSideBar() {
        letter_side_bar.setLetterTouchListener(object : LetterSideBar.LetterTouchListener{
            override fun touch(letter: String?) {
                tv_letter.apply {
                    visibility = View.VISIBLE
                    text = letter
                }
            }

            override fun up() {
                //延迟消失
                mHandle.postDelayed({
                    tv_letter.visibility = View.GONE
                },300)
            }
        })
    }

    private fun initColorTrackText() {
        val valueAnimator: ValueAnimator = ObjectAnimator.ofFloat(0f,1f)
        valueAnimator.duration = 2000
        btn_left_to_right.setOnClickListener {
            color_track_text.setDirection(ColorTrackTextView.Direction.LEFT_TO_RIGHT)
            valueAnimator.addUpdateListener {
                color_track_text.setCurProgress(valueAnimator.animatedValue as Float)
            }
            valueAnimator.start()
        }
        btn_right_to_left.setOnClickListener {
            color_track_text.setDirection(ColorTrackTextView.Direction.RIGHT_TO_LEFT)
            valueAnimator.addUpdateListener {
                color_track_text.setCurProgress(valueAnimator.animatedValue as Float)
            }
            valueAnimator.start()
        }
    }

    private fun initLoadingView() {
        changeLoadingStatus(LoadingView58.Status.CIRCLE,0);
        changeLoadingStatus(LoadingView58.Status.TRIANGLE,1000);
        changeLoadingStatus(LoadingView58.Status.RECTANGLE,2000);
    }

    private lateinit var mHandle: Handler

    private fun changeLoadingStatus(status: LoadingView58.Status, time: Long ) {
        mHandle.postDelayed(object : Runnable{
            override fun run() {
                loading_view.setStatus(status)
            }
        },time)
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

    private fun initQQStepView() {
        step_view.apply {
            setStepMax(4000)
        }

        //属性动画
        val valueAnimator = ObjectAnimator.ofFloat(0f, 3000f)
        valueAnimator.apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(animation: ValueAnimator?) {
                    //这里只能先转成Float，直接先转成Int会崩掉
                    val curStep: Float = animation?.animatedValue as Float
                    step_view.setCurStep(curStep.toInt())
                }
            })
            start()
        }
    }
}
