package com.kotlin.boardproject.service


import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.dto.*
import com.kotlin.boardproject.dto.normalpost.*
import com.kotlin.boardproject.model.BlackPost
import com.kotlin.boardproject.repository.BasePostRepository
import com.kotlin.boardproject.repository.BlackPostRepository
import com.kotlin.boardproject.repository.NormalPostRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val normalPostRepository: NormalPostRepository,
    private val userRepository: UserRepository,
    private val basePostRepository: BasePostRepository,
    private val blackPostRepository: BlackPostRepository,
) : PostService {

    @Transactional
    override fun createNormalPost(
        username: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto {
        // 유저 확인
        val user = userRepository.findByEmail(username) ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")

        // TODO: newbie이면 글 쓰기가 안됨 -> security config

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = createNormalPostRequestDto.toPost(user)

        // user post list에 추가
        post.addPost(user)

        return CreateNormalPostResponseDto(normalPostRepository.save(post).id!!)
    }

    @Transactional(readOnly = true)
    override fun findOneNormalPostById(postId: Long): OneNormalPostResponseDto {
        val post =
            normalPostRepository.findByIdAndStatus(postId, PostStautus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        // TODO: comment 막혀 있으면 정보 제공 x 또한 삭제된 comment는 전달 x
        return post.toOneNormalPostReponseDto()
    }

    @Transactional
    override fun editNormalPost(
        username: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)
        val post = normalPostRepository.findByIdAndStatus(postId, PostStautus.NORMAL)
            ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)
        // TODO: 메시지 어떻게 할지 고민

        post.checkWriter(user)
        post.editPost(editNormalPostRequestDto)

        return EditNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun deleteNormalPost(username: String, postId: Long): DeleteNormalPostResponseDto {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")
        val post = normalPostRepository.findByIdAndStatus(postId, PostStautus.NORMAL)
            ?: throw EntityNotFoundException("존재하지 않는 글 입니다.")

        // TODO: 질문 게시글이면 삭제가 불가능하게 설정한다.

        post.checkWriter(user)
        post.deletePost(user)

        return DeleteNormalPostResponseDto(post.id!!)
    }

    @Transactional
    override fun blackPost(
        username: String,
        postId: Long,
        blackPostRequestDto: BlackPostRequestDto,
    ): BlackPostResponseDto {
        // TODO: 뉴비는 신고 못함 -> security config 로 설정
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")

        val post =
            basePostRepository.findByIdAndStatus(postId, PostStautus.NORMAL)
                ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        val blackPost = BlackPost(user = user, post = post, blackReason = blackPostRequestDto.blackReason)
        blackPostRepository.save(blackPost)

        return BlackPostResponseDto(post.id!!)
    }
}
