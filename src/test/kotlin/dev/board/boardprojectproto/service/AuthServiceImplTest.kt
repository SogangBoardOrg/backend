package dev.board.boardprojectproto.service

import dev.board.boardprojectproto.auth.AuthTokenProvider
import dev.board.boardprojectproto.auth.ProviderType
import dev.board.boardprojectproto.common.config.properties.AppProperties
import dev.board.boardprojectproto.dto.UserSignUpDto
import dev.board.boardprojectproto.model.User
import dev.board.boardprojectproto.repository.UserRepository
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
    private val authTokenProvider: AuthTokenProvider = mockk(relaxed = true)
    private val appProperties: AppProperties = mockk(relaxed = true)
    private val authService: AuthServiceImpl =
        AuthServiceImpl(userRepository, appProperties, authTokenProvider, passwordEncoder)

    private val user: User = User(
        id = UUID.randomUUID(),
        email = "test@test.com",
        password = "test1234!",
        username = "test",
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
