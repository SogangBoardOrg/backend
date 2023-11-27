package com.kotlin.boardproject.global.annotation

import com.kotlin.boardproject.domain.schedule.dto.DayOfWeekTimePairDto
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StartTimeBeforeEndTime(
    val message: String = "StartTime should be before EndTime",
)

@Component
class StartTimeBeforeEndTimeValidator : ConstraintValidator<StartTimeBeforeEndTime, DayOfWeekTimePairDto> {
    override fun isValid(value: DayOfWeekTimePairDto, context: ConstraintValidatorContext?): Boolean {
        return value.startTime.isBefore(value.endTime)
    }
}
