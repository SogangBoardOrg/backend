package dev.board.boardprojectproto.repository

import dev.board.boardprojectproto.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long?>
