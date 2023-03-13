package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.dto.CreatePostRequestDto
import com.kotlin.boardproject.repository.PostRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : PostService {

    @Transactional
    override fun createPost(username: String, createPostRequestDto: CreatePostRequestDto) {
        // 유저 확인
        val user = userRepository.findUserByUsername(username) ?: throw EntityNotFoundException("존재하지 않는 유저 입니다.")

        // 포스트 생성 지금은 그냥 진행 -> 태그 null 값이면 다른 post로 취급?
        val post = createPostRequestDto.toPost(user)

        // user post list에 추가
        post.addPost(user)

        postRepository.save(post)
    }


}
