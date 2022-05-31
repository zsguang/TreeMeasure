package com.example.treemeasure.treeCrown

import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.treemeasure.Dao.TreeCrown
import com.example.treemeasure.MyApplication
import com.example.treemeasure.R

class TreeCrownAdapter(private val treeCrownList: List<TreeCrown>, val handler: Handler) :
    RecyclerView.Adapter<TreeCrownAdapter.TreeCrownViewHolder>() {

    /** 点击item */
    private val CLICK_ITEM = 1
    private val LOAD_TREECROWN_IMAGE2 = 2

    /** recyclerView当前点击的选项 */
    private var currentPosition = -1

    inner class TreeCrownViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val treeImage1: ImageView = view.findViewById(R.id.itemCrownImage1)
        val treeImage2: ImageView = view.findViewById(R.id.itemCrownImage2)
        val treeName: TextView = view.findViewById(R.id.itemCrownName)
        val crownHeight: TextView = view.findViewById(R.id.itemAverageCrownHeight)
        val crownWidth: TextView = view.findViewById(R.id.itemAverageCrownWidth)
        val shootingDate: TextView = view.findViewById(R.id.itemCrownShootingDate)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int,
    ): TreeCrownAdapter.TreeCrownViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tree_crown_item, parent, false)

        val viewHolder = TreeCrownViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            Log.i("TreeCrownActivity", "click RecyclerView position $currentPosition")

            notifyItemChanged(currentPosition)
            currentPosition = viewHolder.adapterPosition
            notifyItemChanged(currentPosition)  // 局部刷新
            // notifyDataSetChanged()    // 全局刷新

            handler.sendMessage(Message().apply {
                what = CLICK_ITEM
                arg1 = currentPosition  //表示点击项在列表的位置
            })
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: TreeCrownAdapter.TreeCrownViewHolder, position: Int) {
        if (treeCrownList.isEmpty()) return
        val treeCrown = treeCrownList[position]

        // 利用加载框架来加载图片
        Glide.with(MyApplication.context)
            .load(treeCrown.imagePath1)
            .apply(RequestOptions().placeholder(R.drawable.icon_feature))
            .into(holder.treeImage1)
        Glide.with(MyApplication.context)
            .load(treeCrown.imagePath2)
            .apply(RequestOptions().placeholder(R.drawable.icon_feature))
            .into(holder.treeImage2)
        holder.treeName.text = treeCrown.treeName
        holder.crownHeight.text = treeCrown.crownHeightAverage.toString()
        holder.crownWidth.text = treeCrown.crownWidthAverage.toString()
        holder.shootingDate.text = treeCrown.shootingDate

        if (currentPosition == position) holder.itemView.setBackgroundColor(Color.LTGRAY)
        else holder.itemView.setBackgroundColor(Color.WHITE)
    }

    override fun getItemCount(): Int = treeCrownList.size
}