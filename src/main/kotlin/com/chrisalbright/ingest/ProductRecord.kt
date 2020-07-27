package com.chrisalbright.ingest

import arrow.core.Option
import java.math.BigDecimal
import java.math.BigInteger

data class ProductRecord(
        val productId: BigInteger,
        val productDescription: String,
        val regularDisplayPrice: String,
        val regularCalculatorPrice: BigDecimal,
        val promotionalDisplayPrice: Option<String>,
        val promotionalCalculatorPrice: Option<BigDecimal>,
        val unitOfMeasure: UnitOfMeasure,
        val productSize: String,
        val taxRate: Option<BigDecimal>) {
    enum class UnitOfMeasure {
        Each, Pound
    }
}
