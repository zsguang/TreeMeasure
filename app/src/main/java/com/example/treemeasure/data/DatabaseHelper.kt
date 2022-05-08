package com.example.treemeasure.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    private val createTreeHeight = "create table TreeHeight (" +
            " id integer primary key autoincrement," +
            "name text," +
            "bottomAngle text," +
            "topAngle text," +
            "shootingDate text," +
            "filePath text," +
            "heightValue text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTreeHeight)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}