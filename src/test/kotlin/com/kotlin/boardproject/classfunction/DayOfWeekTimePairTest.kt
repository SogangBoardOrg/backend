package com.kotlin.boardproject.classfunction

import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.LocalTime

class DayOfWeekTimePairTest : BehaviorSpec({
    // overlap 함수 테스트

    val one = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))
    val two = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0))
    val three = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(14, 0))
    val four = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(15, 0))
    val five = DayOfWeekTimePair(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(12, 0))
    val six = DayOfWeekTimePair(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))
    val seven = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
    val eight = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0))
    val nine = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0))
    val ten = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(13, 0))
    val eleven = DayOfWeekTimePair(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 0))



    Given("시간이 안 겹치는 경우") {
        When("날짜가 안겹치는 경우"){
            Then("false를 반환한다") {
                one overlap five shouldBe false
                one overlap six shouldBe false
            }
        }

        When("아예 시간이 겹치지 않는 경우") {
            Then("false를 반환한다") {
                one overlap four shouldBe false
                one overlap nine shouldBe false
            }
        }

        When("끝 시간과 시작 시간이 같기만 한 경우"){
            Then("false를 반환한다"){
                one overlap three shouldBe false
                one overlap seven shouldBe false
            }
        }
    }

    Given("시간이 겹치는 경우"){
        When("일부분만 겹치는 경우"){
            Then("true를 반환한다"){
                one overlap two shouldBe true
                one overlap eight shouldBe true
            }
        }

        When("한 시간이 전부 들어간 경우"){
            Then("true를 반환한다"){
                one overlap ten shouldBe true
                one overlap eleven shouldBe true
            }
        }

        When("시간이 똑같은 경우"){
            Then("true를 반환한다"){
                one overlap one shouldBe true
            }
        }
    }
})
