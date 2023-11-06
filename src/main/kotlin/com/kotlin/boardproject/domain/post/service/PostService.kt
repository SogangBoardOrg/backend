package com.kotlin.boardproject.domain.post.service

import com.kotlin.boardproject.domain.comment.dto.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.post.dto.BlackPostRequestDto
import com.kotlin.boardproject.domain.post.dto.BlackPostResponseDto
import com.kotlin.boardproject.domain.post.dto.CancelLikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.CancelScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.LikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.MyScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.MyWrittenPostResponseDto
import com.kotlin.boardproject.domain.post.dto.ScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreateNormalPostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreateNormalPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.DeleteNormalPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditNormalPostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditNormalPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.NormalPostByQueryResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.OneNormalPostResponseDto
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
