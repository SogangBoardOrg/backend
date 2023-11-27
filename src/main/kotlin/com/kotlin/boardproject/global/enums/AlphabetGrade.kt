package com.kotlin.boardproject.global.enums

enum class AlphabetGrade(val alphabet: String, val gradeV1: Double, val gradeV2: Double) {
    A_PLUS("A+", 4.5, 4.3),
    A("A", 4.0, 4.0),
    A_MINUS("A-", 3.7, 3.7),
    B_PLUS("B+", 3.3, 3.3),
    B("B", 3.0, 3.0),
    B_MINUS("B-", 2.7, 2.7),
    C_PLUS("C+", 2.3, 2.3),
    C("C", 2.0, 2.0),
    C_MINUS("C-", 1.7, 1.7),
    D_PLUS("D+", 1.3, 1.3),
    D("D", 1.0, 1.0),
    F("F", 0.0, 0.0),
    S("S", 0.0, 0.0),
    ;

    override fun toString(): String {
        return "$alphabet (Grade: $gradeV1, Grade Point: $gradeV2)"
    }
}
