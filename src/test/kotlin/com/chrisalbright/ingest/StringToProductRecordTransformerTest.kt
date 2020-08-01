package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class StringToProductRecordTransformerTest {

    val stringToProductRecord: StringToProductRecord by lazy {
        val stringToInputRecord: StringToInputRecord = SomeStoreProductCatalogTransformer()
        val taxRateStrategy: TaxRateStrategy = RegularTaxRateStrategy(BigDecimal.ONE)
        val unitOfMeasureStrategy: UnitOfMeasureStrategy = ImperialUnitOfMeasureStrategy()
        val inputRecordToProductRecord: InputRecordToProductRecord = InputRecordTransformer(taxRateStrategy, unitOfMeasureStrategy)
        StringToProductRecordTransformer(stringToInputRecord, inputRecordToProductRecord)
    }

    @Test
    fun `Can convert a single record into a product record`() {
        // given
        val inputString = "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz"
        val expected = ProductRecord(BigInteger("80000001"), "Kimchi-flavored white rice", "$5.67", BigDecimal("5.6700"), None, None, ProductRecord.UnitOfMeasure.Each, "18oz", None)

        // when
        val productRecord: Option<ProductRecord> = stringToProductRecord(inputString)

        // then
        assertEquals(Some(expected), productRecord)
    }

    @Test
    fun `Malformed records do not make it through`() {
        // given
        val inputString = "The quick brown fox jumps over the lazy dog"

        // when
        val productRecord: Option<ProductRecord> = stringToProductRecord(inputString)

        // then
        assertEquals(None, productRecord)
    }
}
