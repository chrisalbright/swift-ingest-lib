package com.chrisalbright.ingest

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class InputRecordTest {

    val singularPriceRecord: InputRecord = InputRecord(
            productId = BigInteger("1"),
            description = "Test Product",
            regularSingularPrice = BigDecimal("1.23"),
            regularSplitPrice = BigDecimal.ZERO.setScale(2),
            regularForX = BigInteger.ZERO,
            promotionalSingularPrice = BigDecimal("0.99"),
            promotionalSplitPrice = BigDecimal.ZERO.setScale(2),
            promotionalForX = BigInteger.ZERO,
            flags = Flags(""),
            productSize = "Test Size"
    )


    @TestFactory
    fun `An input record is Taxable or Not Taxable`() =
            listOf(
                    singularPriceRecord.copy(flags = Flags("NNNNYNNN")) to true,
                    singularPriceRecord.copy(flags = Flags("NNNNNNNN")) to false
            ).map { (productRecord: InputRecord, expectedTaxable: Boolean) ->
                DynamicTest.dynamicTest("For ProductRecord with Flags of ${productRecord.flags.fs}, Taxable is $expectedTaxable") {
                    assertEquals(expectedTaxable, productRecord.isTaxable())
                }
            }

    @TestFactory
    fun `An input record is sold By Weight or Not By Weight`() =
            listOf(
                    singularPriceRecord.copy(flags = Flags("NNYNNNNN")) to true,
                    singularPriceRecord.copy(flags = Flags("NNNNNNNN")) to false
            ).map { (productRecord: InputRecord, expectedByWeight: Boolean) ->
                DynamicTest.dynamicTest("For ProductRecord with Flags of ${productRecord.flags.fs}, By Weight is $expectedByWeight") {
                    assertEquals(expectedByWeight, productRecord.isSoldByWeight())
                }
            }
}