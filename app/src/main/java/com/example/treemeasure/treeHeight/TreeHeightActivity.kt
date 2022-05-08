package com.example.treemeasure.treeHeight

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.treemeasure.CameraActivity
import com.example.treemeasure.Dao.TreeHeight
import com.example.treemeasure.MyApplication
import com.example.treemeasure.R
import com.example.treemeasure.databinding.ActivityTreeHeightBinding
import kotlinx.android.synthetic.main.activity_tree_height.*

class TreeHeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTreeHeightBinding

    private lateinit var viewModel: TreeHeightViewModel

    private var treeHeightList: MutableList<TreeHeight> = mutableListOf()

    private lateinit var adapter: TreeHeightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreeHeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(TreeHeightViewModel::class.java)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = TreeHeightAdapter(treeHeightList, handler)
        binding.recyclerView.adapter = adapter


        viewModel.treeHeightList.observe(this, Observer {
            treeHeightList.clear()
            treeHeightList.addAll(it)
            adapter.notifyDataSetChanged()
            Log.d("TreeHeightActivity", treeHeightList.toString())
        })

        binding.btnShooting.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    private val LOAD_TREEHEIGHT = 1
    private val handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            // 在这里可以进行UI操作
            when (msg.what) {
                LOAD_TREEHEIGHT -> {
                    val position = msg.arg1
//                    binding.imageTree.setImageURI(Uri.parse(treeHeightList[position].imagePath))
                    // 利用加载框架来加载图片
                    Glide.with(MyApplication.context)
                        .load(treeHeightList[position].imagePath)
                        .apply(RequestOptions().placeholder(R.drawable.icon_feature))
                        .into(binding.imageTree)
                    binding.textTreeName.text = treeHeightList[position].treeName
                    binding.textPhoneHeight.text = treeHeightList[position].phoneHeight + " 米"
                    binding.textPhoneAngle.text = treeHeightList[position].phoneAngel
                    binding.textSlopeAngle.text = treeHeightList[position].slopeAngle
                    binding.textTreeHeightValue.text = treeHeightList[position].heightValue
                    binding.textTreeDBHValue.text = treeHeightList[position].DBHValue
                    binding.recalculate.text =
                        if (treeHeightList[position].heightValue == "0") "计算树高" else "重新计算"
                }
            }
        }
    }

}