package com.example.treemeasure.treeCrown

import android.os.Handler
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.treemeasure.Dao.TreeCrown
import com.example.treemeasure.Dao.TreeHeight
import com.example.treemeasure.treeHeight.TreeHeightAdapter

class TreeCrownAdapter(private val treeCrownList: List<TreeCrown>, val handler: Handler): RecyclerView.Adapter<TreeHeightAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TreeHeightAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TreeHeightAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}