package com.example.treemeasure

import com.example.treemeasure.Dao.AppDatabase

fun main() {
    val a = 100
    when {
        a > 10 -> println("a>10")
        a > 50 -> println("a>50")
        a > 100 -> println("a>100")
    }
}