package com.example.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandle = Handler(Looper.getMainLooper())
        initLoadingView()
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

}
