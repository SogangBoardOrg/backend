package com.kotlin.boardproject.service

import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.config.properties.AppProperties
import com.kotlin.boardproject.dto.UserSignUpDto
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

class AuthServiceImplTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val passwordEncoder: BCryptPasswordEncoder = mockk(relaxed = true)
    private val authTokenProvider: com.kotlin.boardproject.auth.AuthTokenProvider = mockk(relaxed = true)
    private val appProperties: AppProperties = mockk(relaxed = true)
    private val authService: AuthServiceImpl =
        AuthServiceImpl(userRepository, appProperties, authTokenProvider, passwordEncoder)

    private val user: User = User(
        id = UUID.randomUUID(),
        email = "test@test.com",
        password = "test1234!",
        nickname = "test",
        providerType = ProviderType.LOCAL,
    )

    @Test
    fun 유저_등록_정상() {
        // given
        every { userRepository.save(any()) } returns user

        // when
        val result = authService.saveUser(UserSignUpDto("test@test.com", "test", "test1234!"))

        // then
        verify(exactly = 1) { passwordEncoder.encode("test1234!") }
        verify(exactly = 1) { userRepository.save(any()) }
        Assertions.assertThat(user.id).isEqualTo(result)
    }
}
