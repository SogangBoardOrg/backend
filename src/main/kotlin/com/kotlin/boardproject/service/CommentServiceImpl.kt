package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.NotificationType
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.dto.notification.NotificationCreateDto
import com.kotlin.boardproject.model.BlackComment
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.LikeComment
import com.kotlin.boardproject.repository.*
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class CommentServiceImpl(
    private val postRepository: BasePostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeCommentRepository: LikeCommentRepository,
    private val blackCommentRepository: BlackCommentRepository,
) : CommentService {

    @Transactional
    override fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
        parentCommentId: Long?,
    ): Pair<CreateCommentResponseDto, NotificationCreateDto> {
        log.info("create Comment")
        // user
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        // post
        val post = postRepository.findByIdAndStatusFetchCommentList(createCommentRequestDto.postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${createCommentRequestDto.postId} 에 해당하는 글이 존재하지 않습니다.")

        // 부모 댓글
        val parentComment = parentCommentId?.let {
            commentRepository.findByIdAndStatusFetchAncestorAndPost(parentCommentId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException("$parentCommentId 에 해당하는 댓글이 존재하지 않습니다.")
        }
        // 대댓글에서 부모글을 찾는 쿼리가 나가면 test 실패
        // 1. 부모의 댓글이 없으면 자신이 선조
        // 2. 부모가 있으면 부모의 선조가 있는지 찾음 -> 없으면 자신의 선조를 부모로 지정.
        // 3. 부모가 있고, 선조가 있다면 해당 선조를 자신의 선조로 만든다.
        val ancestorComment = parentComment?.let {
            // 대댓글로 판정되었을 시 로직
            if (parentComment.post != post) {
                throw ConditionConflictException(
                    ErrorCode.CONDITION_NOT_FULFILLED,
                    "부모 댓글의 글 번호 ${parentComment.post.id} 와 현재 글의 번호 ${post.id}" +
                        " 는 일치하지 않습니다",
                )
            }
            parentComment.ancestor ?: parentComment
        }

        val comment = Comment(
            content = createCommentRequestDto.content,
            isAnon = createCommentRequestDto.isAnon,
            writer = user,
            post = post,
            parent = parentComment,
            ancestor = ancestorComment,
        )

        comment.addComment(post)
        // 1. 그냥 댓글이면 post의 writer return
        // 2. 대댓글이면 부모 댓글의 writer return
        val returnComment = commentRepository.save(comment)

        return Pair(
            CreateCommentResponseDto(
                returnComment.id!!,
                post.id!!,
                comment.content,
            ),
            NotificationCreateDto(
                fromUser = user,
                toUser = parentComment?.writer ?: post.writer,
                url = "/post/${post.id}",
                message = comment.content,
                notificationType = NotificationType.COMMENT,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun findMyComment(
        username: String,
        pageable: Pageable,
    ): MyCommentResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        val comments = commentRepository.findByWriterAndStatusFetchPost(user, PostStatus.NORMAL, pageable)

        return MyCommentResponseDto.createDtoFromPageable(comments)
    }

    @Transactional
    override fun updateComment(
        username: String,
        commentId: Long,
        updateCommentRequestDto: UpdateCommentRequestDto,
    ): UpdateCommentResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        val comment = commentRepository.findByIdAndStatusFetchPost(commentId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("$commentId 에 해당하는 댓글이 존재하지 않습니다.")
        // 주인과 일치하는지 확인 -> 질문게시판은 댓글도 수정 못하게?

        comment.checkWriter(user)
        comment.checkPost(updateCommentRequestDto.postId)
        comment.editComment(updateCommentRequestDto)

        return UpdateCommentResponseDto(
            comment.id!!,
            comment.content,
        )
    }

    @Transactional
    override fun deleteComment(
        username: String,
        commentId: Long,
        deleteCommentRequestDto: DeleteCommentRequestDto,
    ): DeleteCommentResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        val comment = commentRepository.findByIdAndStatusFetchPost(commentId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("$commentId 에 해당하는 댓글이 존재하지 않습니다.")
        // 주인과 일치하는지 확인

        comment.checkWriter(user)
        comment.checkPost(deleteCommentRequestDto.postId)
        comment.status = PostStatus.DELETED

        return DeleteCommentResponseDto(
            comment.id!!,
        )
    }

    @Transactional
    override fun likeComment(
        username: String,
        commentId: Long,
    ): LikeCommentResponseDto {
        log.info("like comment start")

        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 에 해당하는 유저가 존재하지 않습니다.")

        val comment =
            commentRepository.findByIdAndStatus(commentId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException("$commentId 에 해당하는 댓글이 존재하지 않습니다.")

        likeCommentRepository.findByUserAndComment(user, comment)?.let {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "이미 추천을 했습니다.")
        }

        val likeComment = LikeComment(
            user = user,
            comment = comment,
        )
        likeCommentRepository.save(likeComment)
        comment.likeComment(likeComment)

        return LikeCommentResponseDto(
            comment.id!!,
        )
    }

    @Transactional
    override fun cancelLikeComment(
        username: String,
        commentId: Long,
    ): CancelLikeCommentResponseDto {
        log.info("cancel like comment start")
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")

        val comment =
            commentRepository.findByIdAndStatusFetchLikeList(commentId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException("존재하지 글 입니다.")

        likeCommentRepository.findByUserAndComment(user, comment)?.let {
            comment.cancelLikeComment(it)
            likeCommentRepository.delete(it)
        }
        log.info("cancel like comment end")
        return CancelLikeCommentResponseDto(comment.id!!)
    }

    @Transactional
    override fun blackComment(
        username: String,
        commentId: Long,
        blackCommentRequestDto: BlackCommentRequestDto,
    ): BlackCommentResponseDto {
        log.info("black comment start")
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")

        val comment =
            commentRepository.findByIdAndStatus(commentId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        blackCommentRepository.findByUserAndComment(user, comment)?.let {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "해당 댓글은 이미 신고가 되었습니다.")
        }

        val blackComment = BlackComment(
            user = user,
            comment = comment,
            blackReason = blackCommentRequestDto.blackReason,
        )
        blackCommentRepository.save(blackComment)
        log.info("black comment end")
        return BlackCommentResponseDto(comment.id!!)
    }
}
