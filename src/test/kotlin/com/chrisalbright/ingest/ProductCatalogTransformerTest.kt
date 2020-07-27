package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class ProductCatalogTransformerTest {
    @Test
    fun `Can transform input string into an InputRecord`() {
        // given
        val inputString = "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz"
        val productCatalogTransformer: ProductCatalogTransformer<String, Option<InputRecord>> = SomeStoreProductCatalogTransformer()

        // when
        val inputRecord: Option<InputRecord> = productCatalogTransformer(inputString)

        // then
        val expected: Some<InputRecord> = Some(InputRecord(BigInteger.valueOf(80000001), "Kimchi-flavored white rice", BigDecimal("5.67"), BigDecimal("0.00"), BigDecimal("0.00"), BigDecimal("0.00"), BigInteger.ZERO, BigInteger.ZERO, Flags("NNNNNNNNN"), "18oz"))
        assertEquals(expected, inputRecord)
    }

    @TestFactory
    fun `Non conforming input strings are ignored`(): List<DynamicTest> {

        val productCatalogTransformer: ProductCatalogTransformer<String, Option<InputRecord>> = SomeStoreProductCatalogTransformer()

        return listOf(
                "Badly Formatted Record" to "this record is completely out of whack",
                "Bad Product ID" to "A0000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz",
                "Bad Regular Singular Price" to "80000001 Kimchi-flavored white rice                                  A0000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz",
                "Bad Promotional Singular Price" to "80000001 Kimchi-flavored white rice                                  00000567 A0000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz",
                "Bad Regular Split Price" to "80000001 Kimchi-flavored white rice                                  00000567 00000000 A0000000 00000000 00000000 00000000 NNNNNNNNN      18oz",
                "Bad Promotional Split Price" to "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 A0000000 00000000 00000000 NNNNNNNNN      18oz",
                "Bad Regular For X" to "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 A0000000 00000000 NNNNNNNNN      18oz",
                "Bad Promotional For X" to "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 A0000000 NNNNNNNNN      18oz"
        ).map { (description, badInput) ->
            DynamicTest.dynamicTest("$description doesn't parse") {
                assertEquals(None, productCatalogTransformer(badInput))
            }
        }
    }

    @Test
    fun `Can transform InputRecord into a ProductRecord`() {
        // given
        val inputRecord = InputRecord(
                productId = BigInteger("80000001"),
                description = "Kimchi-flavored white rice",
                regularSingularPrice = BigDecimal("5.67"),
                promotionalSingularPrice = BigDecimal("0.00"),
                regularSplitPrice = BigDecimal("0.00"),
                promotionalSplitPrice = BigDecimal("0.00"),
                regularForX = BigInteger.ZERO,
                promotionalForX = BigInteger.ZERO,
                flags = Flags("NNNNNNNNN"),
                productSize = "18oz")

        val productCatalogTransformer: ProductCatalogTransformer<InputRecord, ProductRecord> = InputRecordTransformer(BigDecimal::toString, { BigDecimal("0.0775") })

        // when
        val productRecord: ProductRecord = productCatalogTransformer(inputRecord)

        // then
        val expected = ProductRecord(
                productId = BigInteger("80000001"),
                productDescription = "Kimchi-flavored white rice",
                regularDisplayPrice = "$5.67 Each",
                regularCalculatorPrice = BigDecimal("5.67"),
                promotionalDisplayPrice = None,
                promotionalCalculatorPrice = None,
                unitOfMeasure = ProductRecord.UnitOfMeasure.Each,
                productSize = "18oz", taxRate = None)
        assertEquals(expected, productRecord)
    }
}