package com.example.treemeasure.Dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TreeCrown(
    val treeName: String,
    val topAngel: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}