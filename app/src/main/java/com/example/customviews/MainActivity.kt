package com.example.customviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initColorTrackText()
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

}
