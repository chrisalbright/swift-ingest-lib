package com.chrisalbright.ingest.cli

import com.chrisalbright.ingest.*
import java.io.File

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
