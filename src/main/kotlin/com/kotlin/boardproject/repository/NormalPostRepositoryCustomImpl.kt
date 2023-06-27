package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.dto.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.dto.post.normalpost.FindNormalPostByQueryElementDto
import com.kotlin.boardproject.dto.post.normalpost.QFindNormalPostByQueryElementDto
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.QLikePost.likePost
import com.kotlin.boardproject.model.QNormalPost.normalPost
import com.kotlin.boardproject.model.QScrapPost.scrapPost
import com.kotlin.boardproject.model.QUser.user
import com.kotlin.boardproject.model.User
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class NormalPostRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : NormalPostRepositoryCustom {

    override fun findNormalPostByQuery(
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
        pageable: Pageable,
    ): Page<NormalPost> {
        val dataIds = queryFactory
            .select(normalPost.id)
            .from(normalPost)
            .distinct()
            .leftJoin(normalPost.writer).fetchJoin()
            .leftJoin(normalPost.photoList)
            .leftJoin(normalPost.commentList)
            .where(
                writerNoAnonEq(findNormalPostByQueryRequestDto.writerName),
                titleEq(findNormalPostByQueryRequestDto.title),
                contentEq(findNormalPostByQueryRequestDto.content),
                normalTypeEq(findNormalPostByQueryRequestDto.normalType),
                postStatus(PostStatus.NORMAL),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(normalPost.id.desc())
            .fetch()

        val data = queryFactory
            .selectFrom(normalPost)
            .leftJoin(normalPost.writer).fetchJoin()
            .leftJoin(normalPost.photoList).fetchJoin()
            .leftJoin(normalPost.commentList).fetchJoin()
            .where(normalPost.id.`in`(dataIds))
            .orderBy(normalPost.id.desc())
            .fetch()

        return PageImpl(data.toList(), pageable, dataIds.size.toLong())
    }

    override fun findNormalPostByQueryV2(
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
        userEmail: String?,
        pageable: Pageable,
    ): Page<FindNormalPostByQueryElementDto> {
        val searchUser = userEmail?.let { findUserByEmail(userEmail) }

        val scrappedIds = searchUser?.let { findScrappedIds(searchUser) } ?: listOf()
        val likedIds = searchUser?.let { findLikedIds(searchUser) } ?: listOf()

        val dataIds = queryFactory
            .select(normalPost.id)
            .from(normalPost)
            .distinct()
            .where(
                writerNoAnonEq(findNormalPostByQueryRequestDto.writerName),
                titleEq(findNormalPostByQueryRequestDto.title),
                contentEq(findNormalPostByQueryRequestDto.content),
                normalTypeEq(findNormalPostByQueryRequestDto.normalType),
                postStatus(PostStatus.NORMAL),
            )
            .leftJoin(normalPost.writer)
            .leftJoin(normalPost.photoList)
            .leftJoin(normalPost.commentList)
            .leftJoin(normalPost.likeList)
            .leftJoin(normalPost.scrapList)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(normalPost.id.desc())
            .fetch()

        val data = queryFactory
            .select(
                QFindNormalPostByQueryElementDto(
                    normalPost.id,
                    normalPost.title,
                    normalPost.content,
                    normalPost.writer.nickname,
                    normalPost.isAnon,
                    CaseBuilder()
                        .`when`(normalPost.id.`in`(likedIds))
                        .then(true)
                        .otherwise(false),
                    CaseBuilder()
                        .`when`(normalPost.id.`in`(scrappedIds))
                        .then(true)
                        .otherwise(false),
                    CaseBuilder()
                        .`when`(searchUserIsWriter(searchUser)).then(true)
                        .otherwise(false),
                    normalPost.commentOn,
                    normalPost.createdAt,
                    normalPost.updatedAt,
                    normalPost.commentList.size(),
                    normalPost.likeList.size(),
                    normalPost.scrapList.size(),
                    normalPost.photoList.size(),
                ),
            )
            .from(normalPost)
            .leftJoin(normalPost.writer)
            .leftJoin(normalPost.photoList)
            .leftJoin(normalPost.commentList)
            .leftJoin(normalPost.likeList)
            .leftJoin(normalPost.scrapList)
            .where(normalPost.id.`in`(dataIds))
            .orderBy(normalPost.id.desc())
            .fetch()
        return PageImpl(data.toList(), pageable, dataIds.size.toLong())
    }

    private fun findUserByEmail(userEmail: String) =
        queryFactory
            .selectFrom(user)
            .where(user.email.eq(userEmail))
            .fetchOne()

    private fun findScrappedIds(usera: User) =
        queryFactory
            .select(scrapPost.post.id)
            .from(scrapPost)
            .where(scrapPost.user.eq(usera))
            .fetch()
            .toList()

    private fun findLikedIds(usera: User) =
        queryFactory
            .select(likePost.post.id)
            .from(likePost)
            .where(likePost.user.eq(usera))
            .fetch()
            .toList()

    private fun writerNoAnonEq(writerName: String?) =
        if (writerName.isNullOrEmpty()) {
            null
        } else {
            normalPost.writer.nickname.eq(writerName).and(normalPost.isAnon.eq(false))
        }

    private fun titleEq(title: String?) =
        if (title.isNullOrEmpty()) {
            null
        } else {
            normalPost.title.contains(title)
        }

    private fun contentEq(content: String?) =
        if (content.isNullOrEmpty()) {
            null
        } else {
            normalPost.content.contains(content)
        }

    private fun searchUserIsWriter(searchUser: User?): BooleanExpression =
        if (searchUser == null) {
            // TODO: 더 좋은 방법이 없는지 물어보기
            Expressions.FALSE.isTrue()
        } else {
            normalPost.writer.eq(searchUser)
        }

    private fun normalTypeEq(normalType: NormalType?) =
        normalType.let { normalPost.normalType.eq(normalType) }

    private fun postStatus(status: PostStatus): BooleanExpression =
        normalPost.status.eq(status)
}
