package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal

typealias TaxRate = BigDecimal
typealias TaxRateStrategy = (InputRecord) -> Option<TaxRate>

class RegularTaxRateStrategy(private val rate: TaxRate) : TaxRateStrategy {
    override fun invoke(inputRecord: InputRecord): Option<TaxRate> =
            if (inputRecord.isTaxable()) {
                Some(rate)
            } else {
                None
            }
}
