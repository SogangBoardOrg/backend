package com.kotlin.boardproject

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.post.repository.NormalPostRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.NormalType
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import javax.annotation.PostConstruct
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Profile("local")
@Component
class MyDataInit(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val normalPostRepository: NormalPostRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {
    @PostConstruct
    fun init() {
        val (user_a, user_b) = userCreate()
        // a 는 포스트를 쓰고,
        // post 4개 생성 1, 2는 a 가 작성했고, 3, 4는 b가 작성한 post다.
        val (n_post_1, n_post_2) = postCreate(user_a, 1)

        val (n_post_3, n_post_4) = postCreate(user_b, 3)

        // 각각 댓글 6개 생성
        commentCreate(n_post_1, user_a, user_b)
        commentCreate(n_post_2, user_a, user_b)
        commentCreate(n_post_3, user_a, user_b)
        commentCreate(n_post_4, user_a, user_b)
    }

    private fun commentCreate(post: NormalPost, user_a: User, user_b: User) {
        // 1. comment 생성하기
        // 2. comment를 붙이기

        // 1
        val comment1 = Comment(
            content = "comment_1",
            writer = user_a,
            post = post,
            isAnon = true,
        )
        comment1.addComment(post)
        // comment1.joinAncestor(null)
        commentRepository.saveAndFlush(comment1)

        // 2
        // - 3
        val comment2 = Comment(
            content = "comment_2",
            writer = user_b,
            post = post,
            isAnon = false,
        )
        comment2.addComment(post)
        // comment2.joinAncestor(null)
        commentRepository.saveAndFlush(comment2)

        val comment3 = Comment(
            content = "comment_3",
            writer = user_a,
            post = post,
            isAnon = false,
            parent = comment2,
            ancestor = comment2,
        )
        comment3.addComment(post)
        // comment3.joinAncestor(comment2)
        commentRepository.saveAndFlush(comment3)

        // 4
        // - 5
        // | - 6
        val comment4 = Comment(
            content = "comment_4",
            writer = user_b,
            post = post,
            isAnon = true,
        )
        comment4.addComment(post)
        // comment4.joinAncestor(null)
        commentRepository.saveAndFlush(comment4)

        val comment5 = Comment(
            content = "comment_5",
            writer = user_a,
            post = post,
            isAnon = false,
            parent = comment4,
            ancestor = comment4,
        )
        comment5.addComment(post)
        // comment5.joinAncestor(comment4)
        commentRepository.saveAndFlush(comment5)

        val comment6 = Comment(
            content = "comment_6",
            writer = user_b,
            post = post,
            isAnon = true,
            parent = comment5,
            ancestor = comment4,
        )
        comment6.addComment(post)
        // comment6.joinAncestor(comment4)
        commentRepository.saveAndFlush(comment6)

        normalPostRepository.save(post)
    }

    private fun postCreate(user: User, start: Int): List<NormalPost> {
        val post_1 = NormalPost(
            title = "post_$start",
            content = "post_$start",
            writer = user,
            isAnon = true,
            normalType = NormalType.FREE,
            commentOn = true,
        )
        val post_2 = NormalPost(
            title = "post_${start + 1}",
            content = "post_${start + 1}",
            writer = user,
            isAnon = false,
            normalType = NormalType.FREE,
            commentOn = true,
        )
        normalPostRepository.saveAllAndFlush(listOf(post_1, post_2))
        return listOf(post_1, post_2)
    }

    private fun userCreate(): List<User> {
        val customer = User(
            email = "a@test.com",
            password = "a",
            role = Role.ROLE_VERIFIED_USER,
            nickname = "a",
            providerType = ProviderType.LOCAL,
        )
        val encodedPassword1 = passwordEncoder.encode(customer.password)
        customer.encodePassword(encodedPassword1)

        val storeOwner = User(
            email = "b@test.com",
            password = "b",
            role = Role.ROLE_VERIFIED_USER,
            nickname = "b",
            providerType = ProviderType.LOCAL,
        )
        val encodedPassword2 = passwordEncoder.encode(storeOwner.password)
        storeOwner.encodePassword(encodedPassword2)

        userRepository.saveAllAndFlush(listOf(customer, storeOwner))
        userRepository.flush()

        return listOf(customer, storeOwner)
    }
}
