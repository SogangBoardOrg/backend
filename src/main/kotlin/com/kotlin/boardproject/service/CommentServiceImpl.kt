package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.dto.CreateCommentRequestDto
import com.kotlin.boardproject.dto.CreateCommentResponseDto
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
}
