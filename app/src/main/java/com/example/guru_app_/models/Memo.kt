package com.example.guru_app_.models

data class Memo(
    val id: Int? = null,  // ID 필드를 nullable로 설정
    val bookId: Int,
    val title: String,
    val content: String,
    val page: Int?,
    val imagePath: String? = null,
    val createdAt: String,
    val updatedAt: String?
)
