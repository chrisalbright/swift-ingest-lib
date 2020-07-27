package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal
import java.util.*

typealias TaxRateSupplier = () -> BigDecimal

class InputRecordTransformer(val getTaxRate: TaxRateSupplier, val locale: Locale = Locale.US) : ProductCatalogTransformer<InputRecord, ProductRecord> {
    override fun invoke(t: InputRecord): ProductRecord {
        val unitOfMeasure: ProductRecord.UnitOfMeasure = if (t.isSoldByWeight()) {
            ProductRecord.UnitOfMeasure.Pound
        } else {
            ProductRecord.UnitOfMeasure.Each
        }

        val taxRate: Option<BigDecimal> = if (t.isTaxable()) {
            Some(getTaxRate())
        } else {
            None
        }

        val regularPricing: Pricing = Pricing.of(t.regularSingularPrice, t.regularSplitPrice, t.regularForX)
        val promotionalPricing: Option<Pricing> = Some(Pricing.of(t.promotionalSingularPrice, t.promotionalSplitPrice, t.promotionalForX)).filterNot { it == Undefined }

        return ProductRecord(
                t.productId,
                t.description,
                regularPricing.format(locale),
                regularPricing.calculatorPrice,
                promotionalPricing.map { it.format(locale) },
                promotionalPricing.map { it.calculatorPrice },
                unitOfMeasure,
                t.productSize,
                taxRate)
    }
}
