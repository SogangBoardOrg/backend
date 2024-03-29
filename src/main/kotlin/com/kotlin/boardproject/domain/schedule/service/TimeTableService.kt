package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteMyTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.MyTimeTableListResponseDto
import com.kotlin.boardproject.domain.schedule.dto.TimeTableResponseDto

interface TimeTableService {

    fun createTimeTable(
        userEmail: String,
        createTimeTableRequestDto: CreateTimeTableRequestDto,
    ): CreateTimeTableResponseDto

    fun getMyTimeTableList(
        userEmail: String,
    ): MyTimeTableListResponseDto

    fun getTimeTableById(
        userEmail: String,
        timeTableId: Long,
    ): TimeTableResponseDto

    fun deleteMyTimeTableById(
        userEmail: String,
        timeTableId: Long,
    ): DeleteMyTimeTableResponseDto

    fun changeTimeTableVisibility(
        userEmail: String,
        timeTableId: Long,
        isPublic: Boolean,
    ): Long

    fun makeTimeTableMain(
        userEmail: String,
        timeTableId: Long,
    ): Long
}
