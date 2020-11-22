package com.example.customviews.behaviour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.TextureView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.customviews.R
import com.example.customviews.commonadapter.RecyclerCommonAdapter
import com.example.customviews.commonadapter.ViewHolder
import kotlinx.android.synthetic.main.activity_behaviour.*

class BehaviourActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_behaviour)

        //不设置这个，当我们创建Menu得时候，就可能不起作用
        setSupportActionBar(tool_bar)
        val list = mutableListOf("1", "2","3","4","5","6","7","8","9")

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = (object : RecyclerCommonAdapter<String>(this,list,R.layout.item_recycler_view){
            override fun convert(holder: ViewHolder?, item: String?, position: Int) {
                holder?.setText(R.id.item_tv,"1234567689哈哈哈")
            }

        })
    }
}
