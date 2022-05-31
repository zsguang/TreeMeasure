package com.example.treemeasure

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.treemeasure.Dao.AppDatabase
import com.example.treemeasure.databinding.ActivityMainBinding
import com.example.treemeasure.treeCrown.TreeCrownActivity
import com.example.treemeasure.treeHeight.TreeHeightActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // 创建数据库
//        val dbHelper = DatabaseHelper(MyApplication.context, "MeasureTree.db", 1)
//        dbHelper.readableDatabase

        binding.btnTreeHeight.setOnClickListener(this)
        binding.btnTreeCrown.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_treeHeight -> {
                startActivity(Intent(this, TreeHeightActivity::class.java))
            }
            R.id.btn_treeCrown -> {
                startActivity(Intent(this, TreeCrownActivity::class.java))
            }
        }
    }
}