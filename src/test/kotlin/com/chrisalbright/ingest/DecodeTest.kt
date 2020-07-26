package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class DecodeTest {
    @TestFactory
    fun `Can decode currency values`() =
            listOf(
                    "0" to Some(BigDecimal("0.00")),
                    "1" to Some(BigDecimal("0.01")),
                    "10" to Some(BigDecimal("0.10")),
                    "100" to Some(BigDecimal("1.00")),
                    "-0" to Some(BigDecimal("0.00")),
                    "-1" to Some(BigDecimal("-0.01")),
                    "-10" to Some(BigDecimal("-0.10")),
                    "-100" to Some(BigDecimal("-1.00")),
                    "12345" to Some(BigDecimal("123.45")),
                    "TotallyNotANumber" to None,
                    "123PartiallyNotANumber" to None,
                    "-NegativeNotANumber" to None

            )
                    .map { (input: String, expected: Option<BigDecimal>) ->
                        dynamicTest("Decoding $input as currency yields $expected") {
                            assertEquals(expected, input.decodeCurrency())
                        }
                    }

    @TestFactory
    fun `Can decode integer values`() =
            listOf(
                    "0" to Some(BigInteger.ZERO),
                    "1" to Some(BigInteger.ONE),
                    "10" to Some(BigInteger("10")),
                    "100" to Some(BigInteger("100")),
                    "12345" to Some(BigInteger("12345")),
                    "TotallyNotANumber" to None,
                    "123PartiallyNotANumber" to None,
                    "-NegativeNotANumber" to None
            )
                    .map { (input: String, expected: Option<BigInteger>) ->
                        dynamicTest("Decoding $input as number yields $expected") {
                            assertEquals(expected, input.decodeInteger())
                        }
                    }


    @TestFactory
    fun `Can decode string values`() =
            listOf(
                    "left padding    " to "left padding",
                    "   right padding" to "right padding"
            )
                    .map { (input: String, expected: String) ->
                        dynamicTest("Decoding '$input' as string yields '$expected'") {
                            assertEquals(expected, input.decodeString())
                        }
                    }


}