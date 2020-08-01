package com.chrisalbright.ingest

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class ImperialUnitOfMeasureStrategyTest {
    @Test
    fun `By Weight Items are measured in Pounds`() {
        // given
        val getUnitOfMeasure = ImperialUnitOfMeasureStrategy()
        val inputRecord = InputRecord(BigInteger.ZERO, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigInteger.ZERO, BigInteger.ZERO, Flags("NNYNNNNN"), "")

        // when
        val unitOfMeasure: ProductRecord.UnitOfMeasure = getUnitOfMeasure(inputRecord)

        // then
        assertEquals(ProductRecord.UnitOfMeasure.Pound, unitOfMeasure)
    }

    @Test
    fun `Non By Weight Items are measured by Each`() {
        // given
        val getUnitOfMeasure = ImperialUnitOfMeasureStrategy()
        val inputRecord = InputRecord(BigInteger.ZERO, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigInteger.ZERO, BigInteger.ZERO, Flags("NNNNNNNN"), "")

        // when
        val unitOfMeasure: ProductRecord.UnitOfMeasure = getUnitOfMeasure(inputRecord)

        // then
        assertEquals(ProductRecord.UnitOfMeasure.Each, unitOfMeasure)
    }
}