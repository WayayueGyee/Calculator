package com.example.calculator

import com.example.calculator.parser.calculateResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ParserTest {

    @Test
    fun lexicalAnalyze() {

    }

    @Test
    fun calculateResult() {
        val expected = 7
        assertEquals(expected, calculateResult("4 + 3"))
    }
}