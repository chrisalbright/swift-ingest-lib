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
    fun `Can parse input record`() {
        // given
        val inputString = "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz"
        val productCatalogTransformer: ProductCatalogTransformer<String, Option<InputRecord>> = SomeStoreProductCatalogTransformer()

        // when
        val inputRecord: Option<InputRecord> = productCatalogTransformer.apply(inputString)

        // then
        val expected: Some<InputRecord> = Some(InputRecord(BigInteger.valueOf(80000001), "Kimchi-flavored white rice", BigDecimal("5.67"), BigDecimal("0.00"), BigDecimal("0.00"), BigDecimal("0.00"), BigInteger.ZERO, BigInteger.ZERO, Flags("NNNNNNNNN"), "18oz"))
        assertEquals(expected, inputRecord)
    }

    @TestFactory
    fun `Non conforming input records are ignored`(): List<DynamicTest> {

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
                assertEquals(None, productCatalogTransformer.apply(badInput))
            }
        }
    }
}