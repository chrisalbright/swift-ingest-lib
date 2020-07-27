package com.chrisalbright.ingest

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

sealed class Pricing {
    abstract fun format(locale: Locale = Locale.US): String
    abstract val calculatorPrice: BigDecimal

    companion object {
        val ZERO = BigDecimal("0.00")
        fun of(singularPrice: BigDecimal, splitPrice: BigDecimal, forX: BigInteger): Pricing =
                when {
                    singularPrice > BigDecimal.ZERO -> {
                        SinglePricing(singularPrice)
                    }
                    splitPrice > BigDecimal.ZERO -> {
                        SplitPricing(splitPrice, forX)
                    }
                    else -> {
                        Undefined
                    }
                }

        fun scalePrice(price: BigDecimal): BigDecimal = price.setScale(4, RoundingMode.HALF_DOWN)
        fun formatPriceString(locale: Locale, price: BigDecimal): String = DecimalFormat.getCurrencyInstance(locale).format(price)

    }
}

class SinglePricing(private val price: BigDecimal) : Pricing() {
    override fun format(locale: Locale): String = formatPriceString(locale, price)
    override val calculatorPrice: BigDecimal = scalePrice(price)
}

class SplitPricing(private val price: BigDecimal, private val split: BigInteger) : Pricing() {
    override fun format(locale: Locale): String {
        val priceString: String = formatPriceString(locale, price)
        return "$split for $priceString"
    }
    override val calculatorPrice: BigDecimal = scalePrice(price) / split.toBigDecimal()
}

object Undefined : Pricing() {
    override fun format(locale: Locale): String = "Undefined"
    override val calculatorPrice: BigDecimal = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_DOWN)
}
