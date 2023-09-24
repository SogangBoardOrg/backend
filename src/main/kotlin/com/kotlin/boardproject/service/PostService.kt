package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.CommentsByPostIdResponseDto
import com.kotlin.boardproject.dto.post.BlackPostRequestDto
import com.kotlin.boardproject.dto.post.BlackPostResponseDto
import com.kotlin.boardproject.dto.post.CancelLikePostResponseDto
import com.kotlin.boardproject.dto.post.CancelScrapPostResponseDto
import com.kotlin.boardproject.dto.post.LikePostResponseDto
import com.kotlin.boardproject.dto.post.MyScrapPostResponseDto
import com.kotlin.boardproject.dto.post.MyWrittenPostResponseDto
import com.kotlin.boardproject.dto.post.ScrapPostResponseDto
import com.kotlin.boardproject.dto.post.normalpost.CreateNormalPostRequestDto
import com.kotlin.boardproject.dto.post.normalpost.CreateNormalPostResponseDto
import com.kotlin.boardproject.dto.post.normalpost.DeleteNormalPostResponseDto
import com.kotlin.boardproject.dto.post.normalpost.EditNormalPostRequestDto
import com.kotlin.boardproject.dto.post.normalpost.EditNormalPostResponseDto
import com.kotlin.boardproject.dto.post.normalpost.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.dto.post.normalpost.NormalPostByQueryResponseDto
import com.kotlin.boardproject.dto.post.normalpost.OneNormalPostResponseDto
import org.springframework.data.domain.Pageable

interface PostService {

    fun findNormalPostByQuery(
        userEmail: String?,
        pageable: Pageable,
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
    ): NormalPostByQueryResponseDto

    fun findOneNormalPost(
        userEmail: String?,
        postId: Long,
    ): OneNormalPostResponseDto

    fun findMyWrittenPost(
        userEmail: String,
        pageable: Pageable,
    ): MyWrittenPostResponseDto

    fun findMyScrapPost(
        userEmail: String,
        pageable: Pageable,
    ): MyScrapPostResponseDto

    fun createNormalPost(
        userEmail: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto

    fun editNormalPost(
        userEmail: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto

    fun deleteNormalPost(
        userEmail: String,
        postId: Long,
    ): DeleteNormalPostResponseDto

    fun blackPost(
        userEmail: String,
        postId: Long,
        blackPostRequestDto: BlackPostRequestDto,
    ): BlackPostResponseDto

    fun likePost(
        userEmail: String,
        postId: Long,
    ): LikePostResponseDto

    fun cancelLikePost(
        userEmail: String,
        postId: Long,
    ): CancelLikePostResponseDto

    fun scrapPost(
        userEmail: String,
        postId: Long,
    ): ScrapPostResponseDto

    fun cancelScrapPost(
        userEmail: String,
        postId: Long,
    ): CancelScrapPostResponseDto

    fun findCommentsByPostId(
        userEmail: String?,
        postId: Long,
    ): CommentsByPostIdResponseDto
}
