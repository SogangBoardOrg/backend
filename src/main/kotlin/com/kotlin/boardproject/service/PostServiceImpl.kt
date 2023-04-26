package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.PostSearchDto
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
) : PostService {

    @Transactional(readOnly = true)
    override fun findNormalPostByQuery(
        username: String?,
        pageable: Pageable,
        postSearchDto: PostSearchDto,
    ): QueryNormalPostSearchResponseDto {
        log.info(postSearchDto.writerName)
        val user = username?.let {
            userRepository.findByEmail(it)
        }

        val writer = postSearchDto.writerName
            ?.takeIf { it.isNotEmpty() }
            ?.run {
                userRepository.findUserByNickname(this)
            }

        val result = normalPostRepository.findByQuery(
            title = postSearchDto.title,
            content = postSearchDto.content,
            writer = writer,
            normalType = postSearchDto.normalType,
            pageable = pageable,
        )
        // return QueryNormalPostSearchResponseDto(null, 1, 1, 1, 1, 1)
        return QueryNormalPostSearchResponseDto.createDtoFromPageable(result, user)
    }

    @Transactional
    override fun createNormalPost(
        username: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto {
        // 유저 확인
        log.info(username)
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 않는 유저 입니다.")

        // TODO: newbie이면 글 쓰기가 안됨 -> security config

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = createNormalPostRequestDto.toPost(user)

        // user post list에 추가
        post.addPost(user)

        return CreateNormalPostResponseDto(normalPostRepository.save(post).id!!)
    }

    @Transactional(readOnly = true)
    override fun findOneNormalPostById(
        username: String?,
        postId: Long,
    ): OneNormalPostResponseDto {
        val user = username?.let {
            userRepository.findByEmail(it)
        }

        val post =
            normalPostRepository.findPostCustom(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")

        // 댓글 목록 추가,
        log.info(post.toString())
        log.info(post.commentList.toString())
        return post.toOneNormalPostResponseDto(user)
    }

    @Transactional
    override fun editNormalPost(
        username: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)
        val post = normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)
        // TODO: 메시지 어떻게 할지 고민

        post.checkWriter(user)
        post.editPost(editNormalPostRequestDto)

        return EditNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun deleteNormalPost(username: String, postId: Long): DeleteNormalPostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 않는 유저 입니다.")
        val post = normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException("존재하지 않는 글 입니다.")

        // TODO: 질문 게시글이면 삭제가 불가능하게 설정한다.

        post.checkWriter(user)
        post.deletePost(user)

        return DeleteNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun likePost(
        username: String,
        postId: Long,
    ): LikePostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        likePostRepository.findByUserAndPost(user, post)?.let {
            throw ConditionConflictException("이미 추천을 했습니다.")
        }

        val likePost = LikePost(
            user = user,
            post = post,
        )
        likePostRepository.save(likePost)
        post.addLikePost(likePost)

        return LikePostResponseDto(post.id!!)
    }

    @Transactional
    override fun cancelLikePost(
        username: String,
        postId: Long,
    ): CancelLikePostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        likePostRepository.findByUserAndPost(user, post)?.let {
            post.cancelLikePost(it)
            likePostRepository.delete(it)
        }

        return CancelLikePostResponseDto(post.id!!)
    }

    @Transactional
    override fun blackPost(
        username: String,
        postId: Long,
        blackPostRequestDto: BlackPostRequestDto,
    ): BlackPostResponseDto {
        // TODO: 뉴비는 신고 못함 -> security config 로 설정
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("${username}은 존재하지 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        blackPostRepository.findByUserAndPost(user, post)?.let {
            throw ConditionConflictException("해당 글은 이미 신고가 되었습니다.")
        }

        val blackPost = BlackPost(user = user, post = post, blackReason = blackPostRequestDto.blackReason)
        blackPostRepository.save(blackPost)

        return BlackPostResponseDto(post.id!!)
    }

    @Transactional
    override fun scrapPost(
        username: String,
        postId: Long,
    ): ScrapPostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        scrapPostRepository.findByUserAndPost(user, post)?.let {
            throw ConditionConflictException("이미 스크랩을 하였습니다.")
        }

        val scrapPost = ScrapPost(
            user = user,
            post = post,
        )

        scrapPostRepository.save(scrapPost)
        post.addScrapPost(scrapPost)

        return ScrapPostResponseDto(post.id!!)
    }

    @Transactional
    override fun cancelScrapPost(
        username: String,
        postId: Long,
    ): CancelScrapPostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        scrapPostRepository.findByUserAndPost(user, post)?.let {
            post.cancelScrapPost(it)
            scrapPostRepository.delete(it)
        }

        return CancelScrapPostResponseDto(post.id!!)
    }
}
