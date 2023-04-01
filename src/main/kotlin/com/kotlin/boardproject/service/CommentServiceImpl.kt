package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.repository.BasePostRepository
import com.kotlin.boardproject.repository.CommentRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class CommentServiceImpl(
    private val postRepository: BasePostRepository,
    private val userRepository: UserRepository,
    private val commentPostRepository: CommentRepository,
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
            commentPostRepository.save(comment).id!!,
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

        val comment = commentPostRepository.findByIdAndStatus(commentId, PostStautus.NORMAL)
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

        val comment = commentPostRepository.findByIdAndStatus(commentId, PostStautus.NORMAL)
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
}
