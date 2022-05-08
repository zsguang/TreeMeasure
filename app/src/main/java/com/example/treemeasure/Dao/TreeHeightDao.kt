package com.example.treemeasure.Dao

import androidx.room.*

@Dao
interface TreeHeightDao {

    @Insert
    fun insertTreeHeight(treeHeight: TreeHeight): Long

//    @Update
//    fun updateTreeHeight(treeHeight: TreeHeight): Boolean

    @Query("delete from TreeHeight where imagePath = :imagePath")
    fun deleteByImagePath(imagePath: String): Int

    @Query("delete from TreeHeight where id = :mid")
    fun deleteById(mid: String): Int

    @Query("select * from TreeHeight order by id desc limit 1")
    fun getMaxId(): Int

    @Query("select * from TreeHeight order by id desc ")
    fun loadAllTreeHeights(): List<TreeHeight>

    @Query("select id from TreeHeight where treeName = :treeName")
    fun checkTreeName(treeName: String): Int

//    @Query("select * from TreeHeight order by shootingDate")
//    fun loadUsersOlderThan(age: Int): List<TreeHeight>

}