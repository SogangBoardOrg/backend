package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.domain.QBasePost.basePost
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryElementDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryRequestDto
import com.kotlin.boardproject.domain.post.dto.read.QPostByQueryElementDto
import com.kotlin.boardproject.domain.user.domain.QUser
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.enums.PostType
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class BasePostRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : BasePostRepositoryCustom {
    override fun findPostByQuery(
        postByQueryRequestDto: PostByQueryRequestDto,
        postStatus: PostStatus?,
        userEmail: String?,
        pageable: Pageable,
    ): Page<PostByQueryElementDto> {
        val searchUser = userEmail?.let { findUserByEmail(it) }

        val dataIds = queryFactory
            .select(basePost.id)
            .from(basePost)
            .distinct()
            .where(
                writerNoAnonEq(postByQueryRequestDto.writerName),
                titleEq(postByQueryRequestDto.title),
                contentEq(postByQueryRequestDto.content),
                postTypeEq(postByQueryRequestDto.postType),
                courseIdEq(postByQueryRequestDto.courseId),
                postStatus(postStatus),
            )
            .leftJoin(basePost.writer)
            .leftJoin(basePost.course)
            .leftJoin(basePost.photoList)
            .leftJoin(basePost.commentList)
            .leftJoin(basePost.scrapList)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(basePost.id.desc())
            .fetch()

        val totalCnt = queryFactory
            .select(basePost.id)
            .from(basePost)
            .distinct()
            .where(
                writerNoAnonEq(postByQueryRequestDto.writerName),
                titleEq(postByQueryRequestDto.title),
                contentEq(postByQueryRequestDto.content),
                postTypeEq(postByQueryRequestDto.postType),
                courseIdEq(postByQueryRequestDto.courseId),
                postStatus(postStatus),
            )
            .leftJoin(basePost.writer)
            .leftJoin(basePost.course)
            .leftJoin(basePost.photoList)
            .leftJoin(basePost.commentList)
            .leftJoin(basePost.scrapList)
            .fetch()
            .size.toLong()

        val data = queryFactory
            .select(
                QPostByQueryElementDto(
                    basePost.id,
                    basePost.title,
                    basePost.content,
                    CaseBuilder()
                        .`when`(basePost.isAnon.eq(false))
                        .then(basePost.writer.nickname)
                        .otherwise("ANON"),
                    CaseBuilder()
                        .`when`(basePost.isAnon.eq(false))
                        .then(basePost.writer.profileImageUrl)
                        .otherwise(Expressions.nullExpression()),
                    basePost.isAnon,
                    CaseBuilder()
                        .`when`(searchUserIsWriter(searchUser)).then(true)
                        .otherwise(false),
                    basePost.postType,
                    basePost.course.id,
                    basePost.course.yearAndSeason.year,
                    basePost.course.yearAndSeason.season,
                    basePost.course.courseCode,
                    basePost.course.title,
                    basePost.reviewScore,
                    basePost.commentOn,
                    basePost.commentList.size(),
                    Expressions.constant(0),
                    basePost.scrapList.size(),
                    basePost.photoList.size(),
                    basePost.createdAt,
                    basePost.updatedAt,
                ),
            ).distinct()
            .from(basePost)
            .leftJoin(basePost.writer)
            .leftJoin(basePost.course)
            .leftJoin(basePost.photoList)
            .leftJoin(basePost.commentList)
            .leftJoin(basePost.scrapList)
            .where(basePost.id.`in`(dataIds))
            .orderBy(basePost.id.desc())
            .fetch()
        return PageImpl(data.toList(), pageable, totalCnt)
    }

    private fun findUserByEmail(userEmail: String) =
        queryFactory
            .selectFrom(QUser.user)
            .where(QUser.user.email.eq(userEmail))
            .fetchOne()

    private fun writerNoAnonEq(writerName: String?) =
        if (writerName.isNullOrEmpty()) {
            null
        } else {
            basePost.writer.nickname.eq(writerName).and(basePost.isAnon.eq(false))
        }

    private fun titleEq(title: String?) =
        if (title.isNullOrEmpty()) {
            null
        } else {
            basePost.title.contains(title)
        }

    private fun contentEq(content: String?) =
        if (content.isNullOrEmpty()) {
            null
        } else {
            basePost.content.contains(content)
        }

    private fun searchUserIsWriter(searchUser: User?) =
        if (searchUser == null) {
            Expressions.FALSE.isTrue()
        } else {
            basePost.writer.eq(searchUser)
        }

    private fun postStatus(status: PostStatus?) =
        if (status == null) {
            null
        } else {
            basePost.status.eq(status)
        }

    private fun postTypeEq(postType: PostType?) =
        if (postType == null) {
            null
        } else {
            basePost.postType.eq(postType)
        }

    private fun courseIdEq(courseId: Long?) =
        if (courseId == null) {
            null
        } else {
            basePost.course.id.eq(courseId)
        }
}
