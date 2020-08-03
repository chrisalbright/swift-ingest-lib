package com.chrisalbright.ingest

import arrow.core.None
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI

class FileCatalogReaderTest {

    @Test
    fun `FileCatalogReader converts a file to CatalogLines`() {
        // given
        val fileUri: URI = javaClass.getResource("/input-sample.txt").toURI()
        val inputFile = File(fileUri)
        val readFileToCatalogLines = FileCatalogFluxReader()

        // when
        val lines: CatalogLines = readFileToCatalogLines(inputFile)

        // then
        StepVerifier.create(lines)
                .expectNext("80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz")
                .expectNext("14963801 Generic Soda 12-pack                                        00000000 00000549 00001300 00000000 00000002 00000000 NNNNYNNNN   12x12oz")
                .expectNext("40123401 Marlboro Cigarettes                                         00001000 00000549 00000000 00000000 00000000 00000000 YNNNNNNNN          ")
                .expectNext("50133333 Fuji Apples (Organic)                                       00000349 00000000 00000000 00000000 00000000 00000000 NNYNNNNNN        lb")
                .verifyComplete()

    }

    @Test
    fun `File is convertible to CatalogLines`() {
        val fileUri: URI = javaClass.getResource("/input-sample.txt").toURI()
        val inputFile = File(fileUri)

        // when
        val lines: CatalogLines = inputFile.readCatalogLines()

        // then
        StepVerifier.create(lines)
                .expectNext("80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz")
                .expectNext("14963801 Generic Soda 12-pack                                        00000000 00000549 00001300 00000000 00000002 00000000 NNNNYNNNN   12x12oz")
                .expectNext("40123401 Marlboro Cigarettes                                         00001000 00000549 00000000 00000000 00000000 00000000 YNNNNNNNN          ")
                .expectNext("50133333 Fuji Apples (Organic)                                       00000349 00000000 00000000 00000000 00000000 00000000 NNYNNNNNN        lb")
                .verifyComplete()
    }

    @Test
    fun `CatalogLines is convertible to ProductRecords`() {
        // given
        val catalogLine: CatalogLines = Flux.fromIterable(listOf(
                "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz"

        ))
        val stringToProductRecord: StringToProductRecord by lazy {
            val stringToInputRecord: StringToInputRecord = SomeStoreProductCatalogTransformer()
            val taxRateStrategy: TaxRateStrategy = RegularTaxRateStrategy(BigDecimal.ONE)
            val unitOfMeasureStrategy: UnitOfMeasureStrategy = ImperialUnitOfMeasureStrategy()
            val inputRecordToProductRecord: InputRecordToProductRecord = InputRecordTransformer(taxRateStrategy, unitOfMeasureStrategy)
            StringToProductRecordTransformer(stringToInputRecord, inputRecordToProductRecord)
        }


        // when
        val productRecords: ProductRecords = catalogLine.convertToProductRecords(stringToProductRecord)

        // then
        StepVerifier.create(productRecords)
                .expectNext(ProductRecord(BigInteger("80000001"), "Kimchi-flavored white rice", "$5.67", BigDecimal("5.6700"), None, None, ProductRecord.UnitOfMeasure.Each, "18oz", None))
                .verifyComplete()
    }

    @Test
    fun `Only conforming CatalogLines are converted to ProductRecords`() {
        // given
        val catalogLine: CatalogLines = Flux.fromIterable(listOf(
                "This is wrong",
                "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz",
                "So is this"

        ))
        val stringToProductRecord: StringToProductRecord by lazy {
            val stringToInputRecord: StringToInputRecord = SomeStoreProductCatalogTransformer()
            val taxRateStrategy: TaxRateStrategy = RegularTaxRateStrategy(BigDecimal.ONE)
            val unitOfMeasureStrategy: UnitOfMeasureStrategy = ImperialUnitOfMeasureStrategy()
            val inputRecordToProductRecord: InputRecordToProductRecord = InputRecordTransformer(taxRateStrategy, unitOfMeasureStrategy)
            StringToProductRecordTransformer(stringToInputRecord, inputRecordToProductRecord)
        }


        // when
        val productRecords: ProductRecords = catalogLine.convertToProductRecords(stringToProductRecord)

        // then
        StepVerifier.create(productRecords)
                .expectNext(ProductRecord(BigInteger("80000001"), "Kimchi-flavored white rice", "$5.67", BigDecimal("5.6700"), None, None, ProductRecord.UnitOfMeasure.Each, "18oz", None))
                .verifyComplete()
    }
}