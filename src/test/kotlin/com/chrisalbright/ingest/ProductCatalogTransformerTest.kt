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
        val expected: Some<InputRecord> = Some(InputRecord(BigInteger.valueOf(80000001), "Kimchi-flavored white rice", BigDecimal("5.67"), Pricing.ZERO, Pricing.ZERO, Pricing.ZERO, BigInteger.ZERO, BigInteger.ZERO, Flags("NNNNNNNNN"), "18oz"))
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

    @TestFactory
    fun `Can transform InputRecord into a ProductRecord`(): List<DynamicTest> {
        // given
        val inputRecord1 = InputRecord(
                productId = BigInteger("1"),
                description = "Single Priced, No Promotion, Not Taxable",
                regularSingularPrice = BigDecimal("5.67"),
                promotionalSingularPrice = Pricing.ZERO,
                regularSplitPrice = Pricing.ZERO,
                promotionalSplitPrice = Pricing.ZERO,
                regularForX = BigInteger.ZERO,
                promotionalForX = BigInteger.ZERO,
                flags = Flags("NNNNNNNN"),
                productSize = "18oz")

        val expected1 = ProductRecord(
                productId = BigInteger("1"),
                productDescription = "Single Priced, No Promotion, Not Taxable",
                regularDisplayPrice = "$5.67",
                regularCalculatorPrice = BigDecimal("5.6700"),
                promotionalDisplayPrice = None,
                promotionalCalculatorPrice = None,
                unitOfMeasure = ProductRecord.UnitOfMeasure.Each,
                productSize = "18oz", taxRate = None)

        val inputRecord2 = InputRecord(
                productId = BigInteger("2"),
                description = "Single Priced, No Promotion, Taxable",
                regularSingularPrice = BigDecimal("5.67"),
                promotionalSingularPrice = Pricing.ZERO,
                regularSplitPrice = Pricing.ZERO,
                promotionalSplitPrice = Pricing.ZERO,
                regularForX = BigInteger.ZERO,
                promotionalForX = BigInteger.ZERO,
                flags = Flags("NNNNYNNN"),
                productSize = "18oz")

        val expected2 = ProductRecord(
                productId = BigInteger("2"),
                productDescription = "Single Priced, No Promotion, Taxable",
                regularDisplayPrice = "$5.67",
                regularCalculatorPrice = BigDecimal("5.6700"),
                promotionalDisplayPrice = None,
                promotionalCalculatorPrice = None,
                unitOfMeasure = ProductRecord.UnitOfMeasure.Each,
                productSize = "18oz", taxRate = Some(BigDecimal("0.0775")))

        val inputRecord3 = InputRecord(
                productId = BigInteger("3"),
                description = "Single Priced, Promotion, Taxable",
                regularSingularPrice = BigDecimal("5.67"),
                promotionalSingularPrice = BigDecimal("2.99"),
                regularSplitPrice = Pricing.ZERO,
                promotionalSplitPrice = Pricing.ZERO,
                regularForX = BigInteger.ZERO,
                promotionalForX = BigInteger.ZERO,
                flags = Flags("NNNNYNN"),
                productSize = "18oz")

        val expected3 = ProductRecord(
                productId = BigInteger("3"),
                productDescription = "Single Priced, Promotion, Taxable",
                regularDisplayPrice = "$5.67",
                regularCalculatorPrice = BigDecimal("5.6700"),
                promotionalDisplayPrice = Some("$2.99"),
                promotionalCalculatorPrice = Some(BigDecimal("2.9900")),
                unitOfMeasure = ProductRecord.UnitOfMeasure.Each,
                productSize = "18oz", taxRate = Some(BigDecimal("0.0775")))

        val inputRecord4 = InputRecord(
                productId = BigInteger("4"),
                description = "Split Priced, Promotion, Taxable",
                regularSingularPrice = Pricing.ZERO,
                promotionalSingularPrice = Pricing.ZERO,
                regularSplitPrice = BigDecimal("2.34"),
                promotionalSplitPrice = BigDecimal("1.00"),
                regularForX = BigInteger.TWO,
                promotionalForX = BigInteger.TWO,
                flags = Flags("NNNNYNN"),
                productSize = "18oz")

        val expected4 = ProductRecord(
                productId = BigInteger("4"),
                productDescription = "Split Priced, Promotion, Taxable",
                regularDisplayPrice = "2 for $2.34",
                regularCalculatorPrice = BigDecimal("1.1700"),
                promotionalDisplayPrice = Some("2 for $1.00"),
                promotionalCalculatorPrice = Some(BigDecimal("0.5000")),
                unitOfMeasure = ProductRecord.UnitOfMeasure.Each,
                productSize = "18oz", taxRate = Some(BigDecimal("0.0775")))

        val inputRecord5 = InputRecord(
                productId = BigInteger("5"),
                description = "Single Priced, No Promotion, By Weight",
                regularSingularPrice = BigDecimal("3.45"),
                promotionalSingularPrice = Pricing.ZERO,
                regularSplitPrice = Pricing.ZERO,
                promotionalSplitPrice = Pricing.ZERO,
                regularForX = BigInteger.ZERO,
                promotionalForX = BigInteger.ZERO,
                flags = Flags("NNYNNNN"),
                productSize = "lb")

        val expected5 = ProductRecord(
                productId = BigInteger("5"),
                productDescription = "Single Priced, No Promotion, By Weight",
                regularDisplayPrice = "$3.45",
                regularCalculatorPrice = BigDecimal("3.4500"),
                promotionalDisplayPrice = None,
                promotionalCalculatorPrice = None,
                unitOfMeasure = ProductRecord.UnitOfMeasure.Pound,
                productSize = "lb", taxRate = None)


        val inputToProductCatalog: ProductCatalogTransformer<InputRecord, ProductRecord> = InputRecordTransformer({ BigDecimal("0.0775") })

        return listOf(
                inputRecord1 to expected1,
                inputRecord2 to expected2,
                inputRecord3 to expected3,
                inputRecord4 to expected4,
                inputRecord5 to expected5
        ).map { (input, expected) ->
            DynamicTest.dynamicTest("Transforming ${input.description}") {
                assertEquals(expected, inputToProductCatalog(input))
            }
        }
    }
}