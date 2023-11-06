package com.kotlin.boardproject.domain.post.service

import com.kotlin.boardproject.domain.comment.dto.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.post.domain.BlackPost
import com.kotlin.boardproject.domain.post.domain.LikePost
import com.kotlin.boardproject.domain.post.domain.ScrapPost
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
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.domain.post.repository.BlackPostRepository
import com.kotlin.boardproject.domain.post.repository.LikePostRepository
import com.kotlin.boardproject.domain.post.repository.NormalPostRepository
import com.kotlin.boardproject.domain.post.repository.ScrapPostRepository
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val normalPostRepository: NormalPostRepository,
    private val userRepository: UserRepository,
    private val basePostRepository: BasePostRepository,
    private val blackPostRepository: BlackPostRepository,
    private val likePostRepository: LikePostRepository,
    private val scrapPostRepository: ScrapPostRepository,
    private val commentRepository: CommentRepository,
) : PostService {

    @Transactional(readOnly = true)
    override fun findNormalPostByQuery(
        userEmail: String?,
        pageable: Pageable,
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
    ): NormalPostByQueryResponseDto {
        val data = normalPostRepository.findNormalPostByQueryV2(
            findNormalPostByQueryRequestDto = findNormalPostByQueryRequestDto,
            userEmail = userEmail,
            pageable = pageable,
        )

        return NormalPostByQueryResponseDto.createDtoFromPageable(data)
    }

    @Transactional(readOnly = true)
    override fun findOneNormalPost(
        userEmail: String?,
        postId: Long,
    ): OneNormalPostResponseDto {
        val user = userEmail?.let {
            userRepository.findByEmail(it)
        }
        val post = normalPostRepository.findByIdAndStatusFetchPhotoListAndUser(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        normalPostRepository.findByIdAndStatusFetchLikeList(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        normalPostRepository.findByIdAndStatusFetchScrapList(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        // post에서는
        // likeList를 가져오고 그 안에서 user를 다시한번 가져온다.
        // scrapList를 가져오고 그 안에서 user를 다시한번 가져온다.
        // photoList는 그냥 가져온다.
        // commentlist는 가져오지 않는다. -> multiple bag fetch exception 발생하므로 comment가 post를 가지고 있는 것으로 해결한다.

        // comment에서 likelist를 가져오고 거기 안에서 user를 다시한번 가져온다.
        // 댓글애 좋아요 여러개 넣어서 테스트 해보자
        val comments = commentRepository.findByPostFetchLikeListOrderById(post)

        return OneNormalPostResponseDto.fromNormalPost(
            post = post,
            searchUser = user,
            commentList = comments,
        )
    }

    @Transactional(readOnly = true)
    override fun findCommentsByPostId(
        userEmail: String?,
        postId: Long,
    ): CommentsByPostIdResponseDto {
        val user = userEmail?.let {
            userRepository.findByEmail(it)
        }

        val post = normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")

        val comments = commentRepository.findByPostFetchLikeListOrderById(post)

        return CommentsByPostIdResponseDto.fromCommentList(
            post = post,
            searchUser = user,
            commentList = comments,
        )
    }

    @Transactional(readOnly = true)
    override fun findMyWrittenPost(
        userEmail: String,
        pageable: Pageable,
    ): MyWrittenPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val postList = basePostRepository.findByWriterAndStatusOrderByIdDesc(user, PostStatus.NORMAL, pageable)

        return MyWrittenPostResponseDto.createDtoFromPageable(postList)
    }

    @Transactional(readOnly = true)
    override fun findMyScrapPost(
        userEmail: String,
        pageable: Pageable,
    ): MyScrapPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val postList = scrapPostRepository.findByWriterAndStatusOrderByIdDesc(user, PostStatus.NORMAL, pageable)

        return MyScrapPostResponseDto.createDtoFromPageable(postList)
    }

    @Transactional
    override fun createNormalPost(
        userEmail: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto {
        // 유저 확인
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = createNormalPostRequestDto.toPost(user)

        // post.addPost(user)
        return CreateNormalPostResponseDto(normalPostRepository.save(post).id!!)
    }

    @Transactional
    override fun editNormalPost(
        userEmail: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 는 없는 유저 이메일 입니다.")
        val post = normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("존재하지 않는 글 입니다.")

        post.notQuestion()
        post.checkWriter(user)
        post.editPost(editNormalPostRequestDto)

        return EditNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun deleteNormalPost(
        userEmail: String,
        postId: Long,
    ): DeleteNormalPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")
        val post = normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("존재하지 않는 글 입니다.")

        post.notQuestion()
        post.checkWriter(user)
        post.deletePost(user)

        return DeleteNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun likePost(
        userEmail: String,
        postId: Long,
    ): LikePostResponseDto {
        val user = userRepository.findByEmailFetchLikeList(userEmail)
            ?: throw EntityNotFoundException("$userEmail 는 없는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatusFetchLikeList(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        likePostRepository.findByUserAndPost(user, post)?.let {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "이미 추천을 했습니다.")
        }

        val likePost = LikePost(
            user = user,
            post = post,
        )
        likePostRepository.save(likePost)
        post.addLikePost(likePost, user)
        return LikePostResponseDto(post.id!!)
    }

    @Transactional
    override fun cancelLikePost(
        userEmail: String,
        postId: Long,
    ): CancelLikePostResponseDto {
        val user = userRepository.findByEmailFetchLikeList(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatusFetchLikeList(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        likePostRepository.findByUserAndPost(user, post)?.let {
            post.cancelLikePost(it, user)
            likePostRepository.delete(it)
        }

        return CancelLikePostResponseDto(post.id!!)
    }

    @Transactional
    override fun blackPost(
        userEmail: String,
        postId: Long,
        blackPostRequestDto: BlackPostRequestDto,
    ): BlackPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}은 존재하지 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        blackPostRepository.findByUserAndPost(user, post)?.let {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "해당 글은 이미 신고가 되었습니다.")
        }

        val blackPost = BlackPost(user = user, post = post, blackReason = blackPostRequestDto.blackReason)
        blackPostRepository.save(blackPost)
        return BlackPostResponseDto(post.id!!)
    }

    @Transactional
    override fun scrapPost(
        userEmail: String,
        postId: Long,
    ): ScrapPostResponseDto {
        val user = userRepository.findByEmailFetchScrapList(userEmail)
            ?: throw EntityNotFoundException("$userEmail 존재하지 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatusFetchScrapList(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        scrapPostRepository.findByUserAndPost(user, post)?.let {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "이미 스크랩을 하였습니다.")
        }

        val scrapPost = ScrapPost(
            user = user,
            post = post,
        )

        scrapPostRepository.save(scrapPost)
        post.addScrapPost(scrapPost, user)

        return ScrapPostResponseDto(post.id!!)
    }

    @Transactional
    override fun cancelScrapPost(
        userEmail: String,
        postId: Long,
    ): CancelScrapPostResponseDto {
        val user = userRepository.findByEmailFetchScrapList(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatusFetchScrapList(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        scrapPostRepository.findByUserAndPost(user, post)?.let {
            post.cancelScrapPost(it, user)
            scrapPostRepository.delete(it)
        }

        return CancelScrapPostResponseDto(post.id!!)
    }
}
