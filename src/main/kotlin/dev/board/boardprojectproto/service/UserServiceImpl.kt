package dev.board.boardprojectproto.service

import dev.board.boardprojectproto.model.User
import dev.board.boardprojectproto.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    override fun join(username: String, pw: String) {
        var user: User = User(username = username, password = pw)
        userRepository.save(user)
        return
    }
}
