package com.example.treemeasure.Dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TreeCrown(
    val treeName: String,
    val crownHeightAverage: Double?,
    val crownWidthAverage: Double?,
    val shootingDate: String,
    // 图片1
    val phoneAngel1: String,
    val slopeAngle1: String,
    val phoneHeight1: String,
    val azimuth1: String,
    val imagePath1: String,
    var crownHeightValue1: String?,
    val crownWidthValue1: String?,
    // 图片2
    val phoneAngel2: String,
    val slopeAngle2: String,
    val phoneHeight2: String,
    val azimuth2: String,
    val imagePath2: String,
    var crownHeightValue2: String?,
    val crownWidthValue2: String?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}