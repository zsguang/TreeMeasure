package com.example.treemeasure.Dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TreeHeight(
    val treeName: String,
    val phoneAngel: String,
    val slopeAngle: String,
    val phoneHeight: String,
    val shootingDate: String,
    val imagePath: String,
    var heightValue: String?,
    val DBHValue: String?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}