package com.kotlin.boardproject.service

import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.FreePost
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.PostRepository
import com.kotlin.boardproject.repository.UserRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceImplTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var writer: User

    @BeforeEach
    fun start(){
        val user: User = User(
            id = UUID.randomUUID(),
            email = "test@test.com",
            password = "test1234!",
            username = "test",
            providerType = ProviderType.LOCAL,
        )
        writer = userRepository.save(user)
    }

    @AfterEach
    fun cleardb() {
    }

    @Test
    fun 글_정상_등록() {
        // given
        var title = "title_test"
        var content = "content_test"

        // when
        var post = FreePost(
            title = title,
            content = content,
            writer = writer,
            isAnon = false,
            commentOn = true,
        )
        postRepository.save(post)

        // then
        var basePosts = postRepository.findAll()
        basePosts.size shouldBe 1
        basePosts[0].title shouldBe "title_test"
        basePosts[0].content shouldBe "content_test"
    }
}
