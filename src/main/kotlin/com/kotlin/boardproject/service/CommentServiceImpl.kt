package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.LikeComment
import com.kotlin.boardproject.repository.BasePostRepository
import com.kotlin.boardproject.repository.CommentRepository
import com.kotlin.boardproject.repository.LikeCommentRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class CommentServiceImpl(
    private val postRepository: BasePostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeCommentRepository: LikeCommentRepository,
) : CommentService {

    @Transactional
    override fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
    ): CreateCommentResponseDto {
        // user
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        // post
        val post = postRepository.findByIdAndStatus(createCommentRequestDto.postId, PostStautus.NORMAL)
            ?: throw EntityNotFoundException("${createCommentRequestDto.postId} 에 해당하는 글이 존재하지 않습니다.")

        // 대 댓글

        // 선조 댓글

        val comment = Comment(
            content = createCommentRequestDto.content,
            isAnon = createCommentRequestDto.isAnon,
            writer = user,
            post = post,
        )
        comment.addComment(post)

        return CreateCommentResponseDto(
            commentRepository.save(comment).id!!,
        )
    }

    @Transactional
    override fun updateComment(
        username: String,
        commentId: Long,
        updateCommentRequestDto: UpdateCommentRequestDto,
    ): UpdateCommentResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        val comment = commentRepository.findByIdAndStatus(commentId, PostStautus.NORMAL)
            ?: throw EntityNotFoundException("$commentId 에 해당하는 댓글이 존재하지 않습니다.")

        // 주인과 일치하는지 확인

        if (user == comment.writer) {
            comment.content = updateCommentRequestDto.content
        } else {
            throw ConditionConflictException("해당 댓글의 유저가 아닙니다!")
        }

        return UpdateCommentResponseDto(
            comment.id!!,
            comment.content,
        )
    }

    @Transactional
    override fun deleteComment(
        username: String,
        commentId: Long,
    ): DeleteCommentResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        val comment = commentRepository.findByIdAndStatus(commentId, PostStautus.NORMAL)
            ?: throw EntityNotFoundException("$commentId 에 해당하는 댓글이 존재하지 않습니다.")

        // 주인과 일치하는지 확인

        if (user == comment.writer) {
            comment.status = PostStautus.DELETED
        } else {
            throw ConditionConflictException("해당 댓글의 유저가 아닙니다!")
        }

        return DeleteCommentResponseDto(
            comment.id!!,
        )
    }

    @Transactional
    override fun likeComment(
        username: String,
        commentId: Long,
    ): LikeCommentResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw com.kotlin.boardproject.common.exception.EntityNotFoundException("존재하지 않는 유저 입니다.")

        val comment =
            commentRepository.findByIdAndStatus(commentId, PostStautus.NORMAL)
                ?: throw com.kotlin.boardproject.common.exception.EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        likeCommentRepository.findByUserAndComment(user, comment)?.let {
            throw ConditionConflictException("이미 추천을 했습니다.")
        }

        val likeComment = LikeComment(
            user = user,
            comment = comment,
        )
        likeCommentRepository.save(likeComment)

        return LikeCommentResponseDto(
            comment.id!!,
        )
    }
}
