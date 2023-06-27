package com.kotlin.boardproject.dto

import com.kotlin.boardproject.model.LikePost
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.ScrapPost


data class NormalPostByQueryMiddleDto(
    val post: NormalPost,
    val isWriter: Boolean,
    val photoCnt: Int,
    val commentCnt: Int,
    val likeList: List<LikePost>,
    val scrapList: List<ScrapPost>,
)
