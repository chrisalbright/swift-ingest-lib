package com.chrisalbright.ingest

import arrow.core.getOrElse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

typealias CatalogLines = Flux<String>
typealias ProductRecords = Flux<ProductRecord>

interface CatalogReader<I> : Function1<I, CatalogLines>

class FileCatalogFluxReader : CatalogReader<File> {
    override fun invoke(input: File): CatalogLines =
            Flux.create<String> { sink ->
                input.forEachLine { line ->
                    sink.next(line)
                }
                sink.complete()
            }
}

fun File.readCatalogLines(): CatalogLines = FileCatalogFluxReader().invoke(this)

fun CatalogLines.convertToProductRecords(transform: StringToProductRecord): ProductRecords =
        flatMap { s ->
            transform(s).map { Mono.just(it) }.getOrElse { Mono.empty() }
        }

fun catalogTransformerForFile(file: File): StringToProductRecordTransformer {
    // Presumably, some attribute of the file would tell
    // us which implementation to use, but in this case
    // simply return the only one available
    val someStoreTransformer: StringToInputRecord = SomeStoreProductCatalogTransformer()
    val taxRate: TaxRate = taxRateForFile(file)
    val taxRateStrategy = RegularTaxRateStrategy(taxRate)
    val unitOfMeasureStrategy = ImperialUnitOfMeasureStrategy()
    val inputRecordTransformer = InputRecordTransformer(taxRateStrategy, unitOfMeasureStrategy)
    return StringToProductRecordTransformer(someStoreTransformer, inputRecordTransformer)
}

fun taxRateForFile(file: File): TaxRate = TaxRate("0.0775")
