package com.kotlin.boardproject.domain.post.service

import com.kotlin.boardproject.domain.comment.dto.read.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.post.dto.black.BlackPostRequestDto
import com.kotlin.boardproject.domain.post.dto.black.BlackPostResponseDto
import com.kotlin.boardproject.domain.post.dto.create.CreatePostRequestDto
import com.kotlin.boardproject.domain.post.dto.create.CreatePostResponseDto
import com.kotlin.boardproject.domain.post.dto.delete.DeletePostResponseDto
import com.kotlin.boardproject.domain.post.dto.edit.EditPostRequestDto
import com.kotlin.boardproject.domain.post.dto.edit.EditPostResponseDto
import com.kotlin.boardproject.domain.post.dto.like.CancelLikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.like.LikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.read.MyWrittenPostResponseDto
import com.kotlin.boardproject.domain.post.dto.read.OnePostResponseDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryRequestDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.CancelScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.MyScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.ScrapPostResponseDto
import org.springframework.data.domain.Pageable

interface PostService {

    fun findPostByQuery(
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
