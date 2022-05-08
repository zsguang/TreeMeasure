package com.example.treemeasure.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TreeCrownDao {
    @Insert
    fun insertTreeCrown(treeCrown: TreeCrown): Long

//    @Update
//    fun updateTreeCrown(treeHeight: TreeHeight): Boolean

//    @Query("delete from TreeCrown where imagePath = :imagePath")
//    fun deleteByImagePath(imagePath: String): Int

    @Query("delete from TreeCrown where id = :mid")
    fun deleteById(mid: String): Int

    @Query("select * from TreeCrown order by id desc limit 1")
    fun getMaxId(): Int

    @Query("select * from TreeCrown")
    fun loadAllTreeCrowns(): List<TreeCrown>

    @Query("select id from TreeCrown where treeName = :treeName")
    fun checkTreeName(treeName: String): Int
}