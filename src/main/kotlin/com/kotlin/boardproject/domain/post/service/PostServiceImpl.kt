package com.kotlin.boardproject.domain.post.service

import com.kotlin.boardproject.domain.comment.dto.read.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.domain.BlackPost
import com.kotlin.boardproject.domain.post.domain.ScrapPost
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
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryElementDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryRequestDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.CancelScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.MyScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.ScrapPostResponseDto
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.domain.post.repository.BlackPostRepository
import com.kotlin.boardproject.domain.post.repository.ScrapPostRepository
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import com.kotlin.boardproject.global.repository.RedisRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val basePostRepository: BasePostRepository,
    private val blackPostRepository: BlackPostRepository,
    private val scrapPostRepository: ScrapPostRepository,
    private val commentRepository: CommentRepository,
    private val redisRepository: RedisRepository,
) : PostService {

    @Transactional(readOnly = true)
    override fun findPostByQuery(
        userEmail: String?,
        pageable: Pageable,
        postByQueryRequestDto: PostByQueryRequestDto,
    ): PostByQueryResponseDto {
        val data = basePostRepository.findPostByQuery(
            postByQueryRequestDto = postByQueryRequestDto,
            postStatus = PostStatus.NORMAL,
            userEmail = userEmail,
            pageable = pageable,
        )

        mapLikeCnt(data)

        return PostByQueryResponseDto.createDtoFromPageable(data)
    }

    private fun mapLikeCnt(data: Page<PostByQueryElementDto>) {
        val likeCntMap = redisRepository.getPostLikeMap(data.content.map { it.id })
        data.content.forEach {
            it.likeCnt = likeCntMap[it.id] ?: 0
        }
    }

    @Transactional(readOnly = true)
    override fun findOnePost(
        userEmail: String?,
        postId: Long,
    ): OnePostResponseDto {
        val user = userEmail?.let {
            userRepository.findByEmail(it)
        }
        val post = basePostRepository.findByIdAndStatusFetchPhotoListAndUser(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        basePostRepository.findByIdAndStatusFetchScrapList(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        // post에서는
        // scrapList를 가져오고 그 안에서 user를 다시한번 가져온다.
        // photoList는 그냥 가져온다.
        // commentlist는 가져오지 않는다. -> multiple bag fetch exception 발생하므로 comment가 post를 가지고 있는 것으로 해결한다.

        // comment에서 likelist를 가져오고 거기 안에서 user를 다시한번 가져온다.
        // 댓글애 좋아요 여러개 넣어서 테스트 해보자
        val comments = commentRepository.findByPostFetchLikeListOrderById(post)

        return OnePostResponseDto.fromPost(
            post = post,
            searchUser = user,
            commentList = comments,
            likeCnt = redisRepository.getPostLikeCount(postId),
            isLiked = userEmail?.let { redisRepository.userLikesPost(postId, it) } ?: false,
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

        val post = basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
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
    override fun createPost(
        userEmail: String,
        createPostRequestDto: CreatePostRequestDto,
    ): CreatePostResponseDto {
        // 리뷰 타입이면 강의 아이디와 리뷰 점수가 필수
        require(validateReview(createPostRequestDto)) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "리뷰면 강의 아이디와 리뷰 점수가 필수")
        }

        if (createPostRequestDto.postType == PostType.REVIEW) {
            if (createPostRequestDto.courseId == null) {
                throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "강의 아이디가 필요합니다.")
            }
            if (createPostRequestDto.reviewScore == null) {
                throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "리뷰 점수가 필요합니다.")
            }
        }

        // 유저 확인
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        // 해당하는 강의 확인
        val course = createPostRequestDto.courseId?.let {
            courseRepository.findByIdOrNull(it)
                ?: throw EntityNotFoundException("${it}번 강의는 존재하지 않습니다.")
        }

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = BasePost(
            title = createPostRequestDto.title,
            content = createPostRequestDto.content,
            writer = user,
            isAnon = createPostRequestDto.isAnon,
            commentOn = createPostRequestDto.commentOn,
            photoList = createPostRequestDto.photoList,
            postType = createPostRequestDto.postType,
            course = course,
            reviewScore = createPostRequestDto.reviewScore,
        )

        // post.addPost(user)
        return CreatePostResponseDto(basePostRepository.save(post).id!!)
    }

    private fun validateReview(createPostRequestDto: CreatePostRequestDto): Boolean {
        return when {
            createPostRequestDto.postType != PostType.REVIEW -> true
            createPostRequestDto.courseId == null -> false
            createPostRequestDto.reviewScore == null -> false
            else -> true
        }
    }

    @Transactional
    override fun editPost(
        userEmail: String,
        postId: Long,
        editPostRequestDto: EditPostRequestDto,
    ): EditPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 는 없는 유저 이메일 입니다.")
        val post = basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("존재하지 않는 글 입니다.")

        post.notEdit()
        post.checkWriter(user)
        post.editPost(editPostRequestDto)

        return EditPostResponseDto(post.id!!)
    }

    @Transactional
    override fun deletePost(
        userEmail: String,
        postId: Long,
    ): DeletePostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")
        val post = basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("존재하지 않는 글 입니다.")

        post.notEdit()
        post.checkWriter(user)
        post.deletePost(user)

        return DeletePostResponseDto(post.id!!)
    }

    @Transactional
    override fun likePost(
        userEmail: String,
        postId: Long,
    ): LikePostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 는 없는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        redisRepository.setPostLike(postId, userEmail)
        return LikePostResponseDto(post.id!!)
    }

    @Transactional
    override fun cancelLikePost(
        userEmail: String,
        postId: Long,
    ): CancelLikePostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        redisRepository.cancelPostLike(postId, user.email)

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
