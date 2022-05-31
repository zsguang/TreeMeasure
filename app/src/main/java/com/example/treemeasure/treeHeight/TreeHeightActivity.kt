package com.example.treemeasure.treeHeight

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.treemeasure.cameras.CameraActivity
import com.example.treemeasure.Dao.TreeHeight
import com.example.treemeasure.MyApplication
import com.example.treemeasure.R
import com.example.treemeasure.data.CameraType
import com.example.treemeasure.databinding.ActivityTreeHeightBinding
import kotlinx.android.synthetic.main.activity_tree_height.*

class TreeHeightActivity : AppCompatActivity() {
    private val TAG = "TreeHeightActivity"

    private lateinit var binding: ActivityTreeHeightBinding

    private lateinit var viewModel: TreeHeightViewModel

    /** recyclerView的item数据 */
    private var treeHeightList: MutableList<TreeHeight> = mutableListOf()

    private lateinit var adapter: TreeHeightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreeHeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(TreeHeightViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        // item的高度固定不变，设置这个属性能提高性能
        binding.recyclerView.setHasFixedSize(true)
        adapter = TreeHeightAdapter(treeHeightList, handler)
        binding.recyclerView.adapter = adapter

        viewModel.treeHeightList.observe(this, Observer {
            treeHeightList.clear()
            treeHeightList.addAll(it)
            adapter.notifyDataSetChanged()
            Log.d("TreeHeightActivity", treeHeightList.toString())
        })

        binding.btnShooting.setOnClickListener {
            // 启动相机，并表明用来拍摄树高
            val intend = Intent(this, CameraActivity::class.java)
            intend.putExtra("cameraType", CameraType.TreeHeightType)
            startActivity(intend)
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