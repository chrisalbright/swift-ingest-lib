package com.chrisalbright.ingest

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

class FlagTest {
    @TestFactory
    fun `Flags indicate by weight items`() =
            listOf(
                    "YYYYYYYY" to true,
                    "NNYNNNNN" to true,
                    "YYNYYYYY" to false,
                    "NNNNNNNN" to false
            ).map { (input: String, expected: Boolean) ->
                DynamicTest.dynamicTest("Flags $input indicate 'By Weight' is $expected") {
                    assertEquals(expected, Flags(input).isSoldByWeight())
                }
            }

    @TestFactory
    fun `Flags indicate taxable items`() =
            listOf(
                    "YYYYYYYY" to true,
                    "NNNNYNNN" to true,
                    "YYYYNYYY" to false,
                    "NNNNNNNN" to false
            ).map { (input: String, expected: Boolean) ->
                DynamicTest.dynamicTest("Flags $input indicate 'Taxable' is $expected") {
                    assertEquals(expected, Flags(input).isTaxable())
                }
            }
}