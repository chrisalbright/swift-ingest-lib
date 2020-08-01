package com.chrisalbright.ingest

import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal
import java.util.*

class InputRecordTransformer(val getTaxRate: TaxRateStrategy, val getUnitOfMeasure: UnitOfMeasureStrategy, val locale: Locale = Locale.US) : ProductCatalogTransformer<InputRecord, ProductRecord> {
    override fun invoke(t: InputRecord): ProductRecord {
        val unitOfMeasure: ProductRecord.UnitOfMeasure = getUnitOfMeasure(t)

        val taxRate: Option<BigDecimal> = getTaxRate(t)

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
