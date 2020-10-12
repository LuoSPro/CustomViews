package com.example.customviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.view.LayoutInflaterCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater,object : LayoutInflater.Factory2{
            override fun onCreateView(
                parent: View?,
                name: String,
                context: Context,
                attrs: AttributeSet
            ): View? {
                //拦截到View的创建
                if (name == "Button"){
                    val textView = TextView(this@MainActivity)
                    textView.text = "被拦截了"
                    return textView
                }
                return null
            }

            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
                //拦截到View的创建
                if (name == "Button"){
                    val textView = TextView(this@MainActivity)
                    textView.text = "被拦截了"
                    return textView
                }
                return null
            }
        })

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
