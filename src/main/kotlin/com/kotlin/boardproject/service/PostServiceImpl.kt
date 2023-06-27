package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.dto.MyScarpPostResponseDto
import com.kotlin.boardproject.dto.MyWrittenPostResponseDto
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
    ): FindNormalPostByQueryResponseDto {
        log.info("find normal post by query start")
        // likelist fetch, scraplist 2개 발생
//        val user = userEmail?.let {
//            userRepository.findByEmailFetchLikeList(it)
//            userRepository.findByEmailFetchScrapList(it)
//        }

//        val writer = findNormalPostByQueryRequestDto.writerName
//            ?.takeIf { it.isNotEmpty() }
//            ?.run {
//                userRepository.findUserByNickname(this)
//            }
//
//        val result = normalPostRepository.findByQuery(
//            title = findNormalPostByQueryRequestDto.title,
//            content = findNormalPostByQueryRequestDto.content,
//            writer = writer,
//            normalType = findNormalPostByQueryRequestDto.normalType,
//            pageable = pageable,
//        )
        log.info("find normal post by query start")
//        val result2 = normalPostRepository.findNormalPostByQuery(
//            findNormalPostByQueryRequestDto = findNormalPostByQueryRequestDto,
//            pageable = pageable,
//        )
//
        val result3 = normalPostRepository.findNormalPostByQueryV2(
            findNormalPostByQueryRequestDto = findNormalPostByQueryRequestDto,
            userEmail = userEmail,
            pageable = pageable,
        )
        log.info("find normal post by query end")
        // return FindNormalPostByQueryResponseDto.createDtoFromPageable(result2, user)
        return FindNormalPostByQueryResponseDto.createDtoFromPageable(result3)
    }

    @Transactional(readOnly = true)
    override fun findOneNormalPost(
        userEmail: String?,
        postId: Long,
    ): OneNormalPostResponseDto {
        // TODO: 리펙토링
        val user = userEmail?.let {
            userRepository.findByEmail(it)
        }

        val post =
            normalPostRepository.findPostCustom(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException("${postId}번 글은 존재하지 않는 글 입니다.")
        val commentList =
            commentRepository.findByPost(post)

        // 댓글 목록 추가,
        log.info(post.toString())
        log.info(post.commentList.toString())
        return post.toOneNormalPostResponseDto(user, commentList)
    }

    @Transactional(readOnly = true)
    override fun findMyWrittenPost(
        userEmail: String,
        pageable: Pageable,
    ): MyWrittenPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val postList = basePostRepository.findByWriterAndStatus(user, PostStatus.NORMAL, pageable)

        return MyWrittenPostResponseDto.createDtoFromPageable(postList)
    }

    @Transactional(readOnly = true)
    override fun findMyScrapPost(
        userEmail: String,
        pageable: Pageable,
    ): MyScarpPostResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val postList = scrapPostRepository.findByUser(user, pageable)

        return MyScarpPostResponseDto.createDtoFromPageable(postList)
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
        post.addPost(user)
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
            ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)
        val post = normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
            ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)
        // TODO: 메시지 어떻게 할지 고민

        post.checkWriter(user)
        post.editPost(editNormalPostRequestDto)

        return EditNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun deleteNormalPost(userEmail: String, postId: Long): DeleteNormalPostResponseDto {
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
        // TODO: fetch join
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
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
        // TODO: fetch join
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
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
        // TODO: fetch join
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
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
        // TODO: fetch join
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStatus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        scrapPostRepository.findByUserAndPost(user, post)?.let {
            post.cancelScrapPost(it, user)
            scrapPostRepository.delete(it)
        }

        return CancelScrapPostResponseDto(post.id!!)
    }
}
