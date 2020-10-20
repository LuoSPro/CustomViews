package com.example.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lock_pattern_view.setLockPatternListener(object : LockPatternView.LockPatternListener{
            override fun lock(password: String) {
                if (password == "03678"){
                    success_view.apply {
                        visibility = View.VISIBLE
                        text = "成功"
                    }
                    failed_view.visibility = View.GONE
                }else{
                    failed_view.apply {
                        visibility = View.VISIBLE
                        text = "密码错误"
                    }
                    success_view.visibility = View.GONE
                    lock_pattern_view.showSelectError()
                }
            }
        })
    }
}
