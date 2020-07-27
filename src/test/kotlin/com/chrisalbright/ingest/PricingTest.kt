package com.chrisalbright.ingest

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PricingTest {
    @Test
    fun `Pricing for Single Priced Items`() {
        // given
        val singlePrice: BigDecimal = BigDecimal("1.23")
        val splitPrice: BigDecimal = Pricing.ZERO
        val forX: BigInteger = BigInteger.ZERO

        // when
        val pricing: Pricing = Pricing.of(singlePrice, splitPrice, forX)

        // then
        assertTrue(pricing is SinglePricing)
        assertEquals("$1.23", pricing.format())
        assertEquals(BigDecimal("1.2300"), pricing.calculatorPrice)

    }

    @Test
    fun `Pricing for Split Priced Items`() {
        // given
        val singlePrice: BigDecimal = Pricing.ZERO
        val splitPrice: BigDecimal = BigDecimal("2.34")
        val forX: BigInteger = BigInteger("7")

        // when
        val pricing: Pricing = Pricing.of(singlePrice, splitPrice, forX)

        // then
        assertTrue(pricing is SplitPricing)
        assertEquals("7 for $2.34", pricing.format())
        assertEquals(BigDecimal("0.3343"), pricing.calculatorPrice)
    }

    @Test
    fun `Pricing scenario that should never occur`() {
        // given
        val singlePrice: BigDecimal = Pricing.ZERO
        val splitPrice: BigDecimal = Pricing.ZERO
        val forX: BigInteger = BigInteger.ZERO

        // when
        val pricing: Pricing = Pricing.of(singlePrice, splitPrice, forX)

        // then
        assertTrue(pricing is Undefined)
        assertEquals("Undefined", pricing.format())
        assertEquals(BigDecimal("0.0000"), pricing.calculatorPrice)
    }
}