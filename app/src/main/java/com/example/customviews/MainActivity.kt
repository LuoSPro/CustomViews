package com.example.customviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandle = Handler(Looper.getMainLooper())
        initLetterSideBar()
    }

    private lateinit var mHandle: Handler

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
}
