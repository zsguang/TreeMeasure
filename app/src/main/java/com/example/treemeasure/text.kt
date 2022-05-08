package com.example.treemeasure

import com.example.treemeasure.Dao.AppDatabase

fun main() {
    val treeHeightDao = AppDatabase.getDatabase(MyApplication.context).treeHeightDao()
    println(treeHeightDao.deleteByImagePath("/data/user/0/com.example.treemeasure/files/？！。.jpg"))
}