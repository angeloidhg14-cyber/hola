package com.example.model

data class LyricLine(
    val timeMs: Long,
    val text: String,
    val translation: String? = null
)
