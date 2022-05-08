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
import com.example.treemeasure.CameraActivity
import com.example.treemeasure.Dao.TreeCrown
import com.example.treemeasure.databinding.ActivityTreeCrownBinding
import com.example.treemeasure.treeHeight.TreeHeightAdapter
import kotlinx.android.synthetic.main.activity_tree_height.*

class TreeCrownActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTreeCrownBinding

    private lateinit var viewModel: TreeCrownViewModel

    private var treeCrownList: MutableList<TreeCrown> = mutableListOf()

    private lateinit var adapter: TreeCrownAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreeCrownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(TreeCrownViewModel::class.java)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = TreeCrownAdapter(treeCrownList, handler)
        binding.recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = TreeCrownAdapter(treeCrownList, handler)
        binding.recyclerView.adapter = adapter


        viewModel.treeCrownList.observe(this, Observer {
            treeCrownList.clear()
            treeCrownList.addAll(it)
            adapter.notifyDataSetChanged()
            Log.d("TreeHeightActivity", treeCrownList.toString())
        })

        binding.btnShooting.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

    }


    private val LOAD_TREEHEIGHT = 1
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // 在这里可以进行UI操作
            when (msg.what) {
                LOAD_TREEHEIGHT -> {
//                    val position = msg.arg1
////                    binding.imageTree.setImageURI(Uri.parse(treeHeightList[position].imagePath))
//                    // 利用加载框架来加载图片
//                    Glide.with(MyApplication.context)
//                        .load(treeHeightList[position].imagePath)
//                        .apply(RequestOptions().placeholder(R.drawable.icon_feature))
//                        .into(binding.imageTree)
//                    binding.textTreeName.text = treeHeightList[position].treeName
//                    binding.textTopAngle.text = treeHeightList[position].topAngel
//                    binding.textLowerAngle.text = treeHeightList[position].lowerAngel
//                    binding.textShootingDate.text = treeHeightList[position].shootingDate
//                    binding.textTreeHeightValue.text = treeHeightList[position].heightValue
//                    binding.textTreeDBHValue.text = treeHeightList[position].DBHValue
//                    binding.recalculate.text =
//                        if (treeHeightList[position].heightValue == "0") "计算树高" else "重新计算"
                }
            }
        }
    }
}