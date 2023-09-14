package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.comment.CommentsByPostIdResponseDto
import com.kotlin.boardproject.dto.post.*
import com.kotlin.boardproject.dto.post.normalpost.*
import com.kotlin.boardproject.model.BlackPost
import com.kotlin.boardproject.model.LikePost
import com.kotlin.boardproject.model.ScrapPost
import com.kotlin.boardproject.repository.*
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
        log.info("find normal post by query start")
        val data = normalPostRepository.findNormalPostByQueryV2(
            findNormalPostByQueryRequestDto = findNormalPostByQueryRequestDto,
            userEmail = userEmail,
            pageable = pageable,
        )
        log.info("find normal post by query end")
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
        log.info("create normal post start")
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = createNormalPostRequestDto.toPost(user)

        // user post list에 추가
        // post.addPost(user)
        log.info("create normal post end")
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

        // TODO: 질문 게시글이면 삭제가 불가능하게 설정한다.

        post.checkWriter(user)
        post.deletePost(user)

        return DeleteNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun likePost(
        userEmail: String,
        postId: Long,
    ): LikePostResponseDto {
        log.info("like post start")
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
        log.info("like post end")
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
        log.info("black post start")

        // TODO: 뉴비는 신고 못함 -> security config 로 설정
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
        log.info("black post end")
        return BlackPostResponseDto(post.id!!)
    }

    @Transactional
    override fun scrapPost(
        userEmail: String,
        postId: Long,
    ): ScrapPostResponseDto {
        val user = userRepository.findByEmailFetchScrapList(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

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
