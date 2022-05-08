package com.example.treemeasure.treeHeight

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.treemeasure.Dao.TreeHeight
import com.example.treemeasure.MyApplication
import com.example.treemeasure.R
import com.example.treemeasure.data.TreeHeightItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TreeHeightAdapter(private val treeList: List<TreeHeight>, val handler: Handler) :
    RecyclerView.Adapter<TreeHeightAdapter.ViewHolder>() {

    private val LOAD_TREEHEIGHT = 1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val treeImage: ImageView = view.findViewById(R.id.treeImage)
        val treeName: TextView = view.findViewById(R.id.treeName)
        val treeHeightValue: TextView = view.findViewById(R.id.treeHeightValue)
        val treeDBHValue: TextView = view.findViewById(R.id.treeDBHValue)
        val shootingDate: TextView = view.findViewById(R.id.shootingDate)
    }

    private var isClick = false
    private var currentPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_item, parent, false)

        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
//            Toast.makeText(parent.context, "you clicked view ", Toast.LENGTH_SHORT).show()
            // 局部刷新
            notifyItemChanged(currentPosition)
            currentPosition = viewHolder.adapterPosition
            notifyItemChanged(currentPosition)
//            notifyDataSetChanged()    // 全局刷新

            handler.sendMessage(Message().apply {
                what = LOAD_TREEHEIGHT
                arg1 = currentPosition  //表示点击项在列表的位置
            })
        }

        return viewHolder
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (treeList.isEmpty()) return
        val treeHeight = treeList[position]

        // holder.treeImage.setImageURI(Uri.parse(treeHeight.imagePath))
        // 利用加载框架来加载图片
        Glide.with(MyApplication.context)
            .load(treeHeight.imagePath)
            .apply(RequestOptions().placeholder(R.drawable.icon_feature))
            .into(holder.treeImage)

        holder.treeName.text = treeHeight.treeName
        holder.treeHeightValue.text = treeHeight.heightValue
        holder.treeDBHValue.text = treeHeight.DBHValue
        holder.shootingDate.text = treeHeight.shootingDate

        if (currentPosition == position) holder.itemView.setBackgroundColor(Color.LTGRAY)
        else holder.itemView.setBackgroundColor(Color.WHITE)

    }

    override fun getItemCount() = treeList.size

    private fun loadTreeHeightData(position: Int) {

    }

}
