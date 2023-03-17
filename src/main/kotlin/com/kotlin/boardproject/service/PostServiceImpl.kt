package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.dto.CreatePostRequestDto
import com.kotlin.boardproject.dto.ReadOnePostResponseDto
import com.kotlin.boardproject.repository.PostRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : PostService {

    @Transactional
    override fun createPost(username: String, createPostRequestDto: CreatePostRequestDto) {
        // 유저 확인
        val user = userRepository.findByEmail(username) ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")

        // TODO: newbie이면 글 쓰기가 안됨

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = createPostRequestDto.toPost(user)

        // user post list에 추가
        post.addPost(user)

        postRepository.save(post)
    }

    @Transactional(readOnly = true)
    override fun readOnePost(postId: Long): ReadOnePostResponseDto {
        val post = postRepository.findByIdOrNull(postId) ?: throw EntityNotFoundException(ErrorCode.NOT_FOUND_ENTITY.message)

        return ReadOnePostResponseDto(
            id = post.id!!,
            commentOn = post.commentOn,
            title = post.title,
            content = post.title,
            writerName = post.writer.username,
            createTime = post.createdAt!!,
            lastModifiedTime = post.updatedAt,
        )
    }
}
