package com.kotlin.boardproject

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.domain.schedule.domain.Course
import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import com.kotlin.boardproject.global.enums.Seasons
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalTime
import javax.annotation.PostConstruct

@Profile("local")
@Component
class MyDataInit(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val basePostRepository: BasePostRepository,
    private val scheduleRepository: ScheduleRepository,
    private val timeTableRepository: TimeTableRepository,
    private val courseRepository: CourseRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {
    @PostConstruct
    fun init() {
        val (user_a, user_b) = userCreate()
        val (timetable_1, timetable_2, timetable_3) = timetableCreate(user_a)

        scheduleAdder(timetable_1)
        scheduleAdder(timetable_2)
        scheduleAdder(timetable_3)

        makeCourse()

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

    private fun makeCourse() {
        val course_1 = Course(
            title = "course_1",
            dayOfWeekTimePairs = listOf(
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(1, 0),
                    endTime = LocalTime.of(3, 0),
                ),
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.TUESDAY,
                    startTime = LocalTime.of(1, 0),
                    endTime = LocalTime.of(3, 0),
                ),
            ).toMutableList(),
            credit = 2.5F,
            majorDepartment = "majorDepartment_1",
            professor = "professor_1",
            locaton = "location_1",
            courseCode = "courseCode_1",
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        )

        val course_2 = Course(
            title = "course_2",
            dayOfWeekTimePairs = listOf(
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.THURSDAY,
                    startTime = LocalTime.of(21, 0),
                    endTime = LocalTime.of(23, 0),
                ),
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.FRIDAY,
                    startTime = LocalTime.of(2, 0),
                    endTime = LocalTime.of(12, 0),
                ),
            ).toMutableList(),
            credit = 2.5F,
            majorDepartment = "majorDepartment_1",
            professor = "professor_1",
            locaton = "location_1",
            courseCode = "courseCode_2",
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        )

        val course_3 = Course(
            title = "course_3",
            dayOfWeekTimePairs = listOf(
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.THURSDAY,
                    startTime = LocalTime.of(21, 0),
                    endTime = LocalTime.of(23, 0),
                ),
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.FRIDAY,
                    startTime = LocalTime.of(2, 0),
                    endTime = LocalTime.of(12, 0),
                ),
            ).toMutableList(),
            credit = 2.5F,
            majorDepartment = "majorDepartment_1",
            professor = "professor_1",
            locaton = "location_1",
            courseCode = "courseCode_3",
            yearAndSeason = YearAndSeason(
                year = 2023,
                season = Seasons.SPRING,
            ),
        )

        courseRepository.saveAllAndFlush(listOf(course_1, course_2, course_3))
    }

    private fun scheduleAdder(timeTable: TimeTable) {
        val schedule_1 = Schedule(
            title = "schedule_1",
            memo = "schedule_1",
            timeTable = timeTable,
            dayOfWeekTimePairs = listOf(
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(1, 0),
                    endTime = LocalTime.of(3, 0),
                ),
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.TUESDAY,
                    startTime = LocalTime.of(1, 0),
                    endTime = LocalTime.of(3, 0),
                ),
            ).toMutableList(),
            alphabetGrade = null,
            credit = 2.5F,
            isMajor = true,
            professor = "professor_1",
            location = "location_1",
            majorDepartment = "majorDepartment_1",
        )

        val schedule_2 = Schedule(
            title = "schedule_2",
            memo = "schedule_2",
            timeTable = timeTable,
            dayOfWeekTimePairs = listOf(
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.THURSDAY,
                    startTime = LocalTime.of(21, 0),
                    endTime = LocalTime.of(23, 0),
                ),
                DayOfWeekTimePair(
                    dayOfWeek = DayOfWeek.FRIDAY,
                    startTime = LocalTime.of(2, 0),
                    endTime = LocalTime.of(12, 0),
                ),
            ).toMutableList(),
            alphabetGrade = null,
            credit = 2.5F,
            isMajor = true,
            professor = "professor_1",
            location = "location_1",
            majorDepartment = "majorDepartment_1",
        )

        scheduleRepository.saveAllAndFlush(listOf(schedule_1, schedule_2))
    }

    private fun timetableCreate(userA: User): List<TimeTable> {
        val timetable_1 = TimeTable(
            title = "timetable_1",
            user = userA,
            isPublic = true,
            isMain = true,
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        )
        val timetable_2 = TimeTable(
            title = "timetable_2",
            user = userA,
            isPublic = false,
            isMain = false,
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        )

        val timeTable_3 = TimeTable(
            title = "timetable_3",
            user = userA,
            isPublic = false,
            isMain = true,
            yearAndSeason = YearAndSeason(
                year = 2022,
                season = Seasons.SPRING,
            ),
        )

        timeTableRepository.saveAllAndFlush(listOf(timetable_1, timetable_2, timeTable_3))
        return listOf(timetable_1, timetable_2, timeTable_3)
    }

    private fun commentCreate(post: BasePost, user_a: User, user_b: User) {
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

        basePostRepository.save(post)
    }

    private fun postCreate(user: User, start: Int): List<BasePost> {
        val post_1 = BasePost(
            title = "post_$start",
            content = "post_$start",
            writer = user,
            isAnon = true,
            commentOn = true,
            postType = PostType.FREE,
        )
        val post_2 = BasePost(
            title = "post_${start + 1}",
            content = "post_${start + 1}",
            writer = user,
            isAnon = false,
            commentOn = true,
            postType = PostType.FREE,
        )
        basePostRepository.saveAllAndFlush(listOf(post_1, post_2))
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
        return listOf(customer, storeOwner)
    }
}
