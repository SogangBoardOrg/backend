package dev.board.boardprojectproto.service

import org.springframework.stereotype.Service

interface PostService{
    // create 맴버 id, dto
    fun writePost()

    // read dto
    fun readPost()

    // update 맴버 id, dto
    fun updatePost()

    // delete 맴버 id, dto
    fun deletePost()
}
