package com.kotlin.boardproject.domain.post.service

import com.kotlin.boardproject.domain.comment.dto.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.post.dto.BlackPostRequestDto
import com.kotlin.boardproject.domain.post.dto.BlackPostResponseDto
import com.kotlin.boardproject.domain.post.dto.CancelLikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.CancelScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.LikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.MyScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.MyWrittenPostResponseDto
import com.kotlin.boardproject.domain.post.dto.PostByQueryResponseDto
import com.kotlin.boardproject.domain.post.dto.ScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreatePostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreatePostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.DeletePostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditPostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.OnePostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.PostByQueryRequestDto
import org.springframework.data.domain.Pageable

interface PostService {

    fun findNormalPostByQuery(
        userEmail: String?,
        pageable: Pageable,
        postByQueryRequestDto: PostByQueryRequestDto,
    ): PostByQueryResponseDto

    fun findOnePost(
        userEmail: String?,
        postId: Long,
    ): OnePostResponseDto

    fun findMyWrittenPost(
        userEmail: String,
        pageable: Pageable,
    ): MyWrittenPostResponseDto

    fun findMyScrapPost(
        userEmail: String,
        pageable: Pageable,
    ): MyScrapPostResponseDto

    fun createPost(
        userEmail: String,
        createPostRequestDto: CreatePostRequestDto,
    ): CreatePostResponseDto

    fun editPost(
        userEmail: String,
        postId: Long,
        editPostRequestDto: EditPostRequestDto,
    ): EditPostResponseDto

    fun deletePost(
        userEmail: String,
        postId: Long,
    ): DeletePostResponseDto

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
