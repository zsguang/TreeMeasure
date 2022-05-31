package com.example.treemeasure.treeCrown

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.treemeasure.cameras.CameraActivity
import com.example.treemeasure.Dao.TreeCrown
import com.example.treemeasure.data.CameraType
import com.example.treemeasure.databinding.ActivityTreeCrownBinding
//import kotlinx.android.synthetic.main.activity_tree_crown.*

/**
 * 树冠页面
 */
class TreeCrownActivity : AppCompatActivity() {

    private val TAG = "TreeCrownActivity"

    private lateinit var binding: ActivityTreeCrownBinding

    private lateinit var viewModel: TreeCrownViewModel

    /** recyclerView的item数据 */
    private var treeCrownList: MutableList<TreeCrown> = mutableListOf()

    private lateinit var adapter: TreeCrownAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreeCrownBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i(TAG, "TreeCrownActivity onCreate()")

        viewModel = ViewModelProvider(this).get(TreeCrownViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        adapter = TreeCrownAdapter(treeCrownList, handler)
        binding.recyclerView.adapter = adapter

        viewModel.treeCrownList.observe(this, Observer {
            treeCrownList.clear()
            treeCrownList.addAll(it)
            adapter.notifyDataSetChanged()
            Log.d("TreeCrownActivity", treeCrownList.toString())
        })

        binding.btnShooting.setOnClickListener {
            // 启动相机，并表明用来拍摄树高
            val intend = Intent(this, CameraActivity::class.java)
            intend.putExtra("cameraType", CameraType.TreeCrownType1)
            startActivity(intend)
        }
    }


    private val CLICK_ITEM = 1
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // 在这里可以进行UI操作
            when (msg.what) {
                CLICK_ITEM -> {

                }
            }
        }
    }
}