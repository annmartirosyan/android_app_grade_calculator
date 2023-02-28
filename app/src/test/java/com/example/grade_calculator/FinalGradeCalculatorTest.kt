package com.example.grade_calculator

import org.junit.Test
import org.junit.Assert.*


class FinalGradeCalculatorTest {

    @Test
    fun `test calculateFinalGrade`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mainActivity = MainActivity()
        val average = 85.0
        val attendanceGrade = 90
        val groupPresentationGrade = 80
        val midterm1Grade = 75
        val midterm2Grade = 85
        val finalProjectGrade = 90
        val expectedFinalGrade = 85.0 * 0.2 +
                90 * 0.1 +
                80 * 0.1 +
                75 * 0.1 +
                85 * 0.2 +
                90 * 0.3


        val actualFinalGrade = mainActivity.calculateFinalGrade(
            average,
            attendanceGrade,
            groupPresentationGrade,
            midterm1Grade,
            midterm2Grade,
            finalProjectGrade
        )

        assertEquals(expectedFinalGrade, actualFinalGrade, 0.001)
    }
}