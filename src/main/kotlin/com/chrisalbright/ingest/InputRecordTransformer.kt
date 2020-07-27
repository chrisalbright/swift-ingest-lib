package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

typealias CurrencyFormatter = (BigDecimal) -> String
typealias TaxRateSupplier = () -> BigDecimal

sealed class Pricing {
    abstract fun format(locale: Locale = Locale.US): String
    abstract fun calculatorPrice(): BigDecimal

    companion object {
        fun of(singularPrice: BigDecimal, splitPrice: BigDecimal, forX: BigInteger): Pricing =
                when {
                    singularPrice > BigDecimal.ZERO -> {
                        SinglePricing(singularPrice)
                    }
                    splitPrice > BigDecimal.ZERO -> {
                        SplitPricing(splitPrice, forX)
                    }
                    else -> {
                        NoPrice
                    }
                }
    }
}

class SinglePricing(private val price: BigDecimal) : Pricing() {
    override fun format(locale: Locale): String {
        val priceString: String = DecimalFormat.getCurrencyInstance(locale).format(price)
        return "$priceString Each"
    }
    override fun calculatorPrice(): BigDecimal = price
}

class SplitPricing(private val price: BigDecimal, private val split: BigInteger) : Pricing() {
    override fun format(locale: Locale): String {
        val priceString: String = DecimalFormat.getCurrencyInstance(locale).format(price)
        return "$split for $priceString"
    }
    override fun calculatorPrice(): BigDecimal = price
}

object NoPrice : Pricing() {
    override fun format(locale: Locale): String = "Unknown"
    override fun calculatorPrice(): BigDecimal = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_DOWN)
}

class InputRecordTransformer(val formatCurrency: CurrencyFormatter, val getTaxRate: TaxRateSupplier, val locale: Locale = Locale.US) : ProductCatalogTransformer<InputRecord, ProductRecord> {
    override fun invoke(t: InputRecord): ProductRecord {
        val unitOfMeasure: ProductRecord.UnitOfMeasure = if (t.flags.isSoldByWeight()) {
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
        val promotionalPricing: Option<Pricing> = Some(Pricing.of(t.promotionalSingularPrice, t.promotionalSplitPrice, t.promotionalForX)).filterNot { it == NoPrice }

        return ProductRecord(
                t.productId,
                t.description,
                regularPricing.format(locale),
                regularPricing.calculatorPrice(),
                promotionalPricing.map { it.format(locale) },
                promotionalPricing.map { it.calculatorPrice() },
                unitOfMeasure,
                t.productSize,
                taxRate)
    }
}
