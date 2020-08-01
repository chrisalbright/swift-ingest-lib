package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class RegularTaxRateStrategyTest {
    @Test
    fun `Taxable products yield a tax rate`() {
        // given
        val getTaxRate = RegularTaxRateStrategy(BigDecimal.ONE)
        val inputRecord = InputRecord(BigInteger.ZERO, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigInteger.ZERO, BigInteger.ZERO, Flags("NNNNYNNN"), "")

        // when
        val rate: Option<BigDecimal> = getTaxRate(inputRecord)

        // then
        assertEquals(Some(BigDecimal.ONE), rate)
    }

    @Test
    fun `Non-Taxable products yield no tax rate`() {
        // given
        val getTaxRate = RegularTaxRateStrategy(BigDecimal.ONE)
        val inputRecord = InputRecord(BigInteger.ZERO, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigInteger.ZERO, BigInteger.ZERO, Flags("NNNNNNNN"), "")

        // when
        val rate: Option<BigDecimal> = getTaxRate(inputRecord)

        // then
        assertEquals(None, rate)
    }
}